package com.example.cv.mail.service;

import com.example.cv.job.document.JobDocument;
import com.example.cv.job.service.JobService;
import com.example.cv.subscriber.document.SubscriberDocument;
import com.example.cv.subscriber.repository.SubscriberRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class EmailService {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            .withZone(ZoneId.of("Asia/Ho_Chi_Minh"));
    private final JavaMailSender mailSender;
    private final SubscriberRepository subscriberRepository;
    private final JobService jobService;
    private final String senderEmail;

    public EmailService(JavaMailSender mailSender, SubscriberRepository subscriberRepository, JobService jobService,
                        @Value("${spring.mail.username:}") String senderEmail) {
        this.mailSender = mailSender;
        this.subscriberRepository = subscriberRepository;
        this.jobService = jobService;
        this.senderEmail = senderEmail;
    }

    public Map<String, Object> sendJobNotificationManually() {
        return sendMatchingJobs("🎯 Jobs phù hợp với skills của bạn", "new-job");
    }

    public void sendDailyJobNotification() {
        sendMatchingJobs("🎯 Jobs phù hợp với skills của bạn", "new-job");
    }

    public void sendWeeklyJobSummary() {
        List<SubscriberDocument> subscribers = subscriberRepository.findAllByIsDeletedFalse();
        List<JobDocument> jobs = jobService.all(50);
        if (subscribers.isEmpty() || jobs.isEmpty()) {
            return;
        }
        int sent = 0;
        for (SubscriberDocument subscriber : subscribers) {
            try {
                send(subscriber.getEmail(), "📊 Tổng kết việc làm tuần này (" + jobs.size() + " vị trí)",
                        render("Tổng kết việc làm tuần này", subscriber, jobs));
                sent++;
            } catch (Exception ignored) {
                // Một subscriber lỗi không làm dừng toàn bộ batch.
            }
        }
    }

    private Map<String, Object> sendMatchingJobs(String subject, String templateName) {
        List<SubscriberDocument> subscribers = subscriberRepository.findAllByIsDeletedFalse();
        if (subscribers.isEmpty()) {
            return Map.of("message", "Không có subscribers nào trong database", "subscribersCount", 0, "emailsSent", 0);
        }
        List<JobDocument> jobs = jobService.all(100);
        int sent = 0;
        for (SubscriberDocument subscriber : subscribers) {
            List<JobDocument> matching = jobs.stream().filter(job -> hasMatchingSkill(job, subscriber)).toList();
            if (matching.isEmpty()) {
                continue;
            }
            try {
                send(subscriber.getEmail(), subject + " (" + matching.size() + " vị trí)",
                        render(subject, subscriber, matching));
                sent++;
            } catch (Exception ignored) {
                // Giữ hành vi batch: tiếp tục gửi cho subscriber tiếp theo.
            }
        }
        return Map.of("message", "Đã gửi email cho " + sent + "/" + subscribers.size() + " subscribers có jobs phù hợp",
                "subscribersCount", subscribers.size(), "emailsSent", sent);
    }

    private boolean hasMatchingSkill(JobDocument job, SubscriberDocument subscriber) {
        List<String> subscriberSkills = subscriber.getSkills() == null ? List.of() : subscriber.getSkills();
        List<String> jobSkills = job.getSkills() == null ? List.of() : job.getSkills();
        return jobSkills.stream().anyMatch(subscriberSkills::contains);
    }

    private void send(String to, String subject, String html) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        if (senderEmail != null && !senderEmail.isBlank()) {
            helper.setFrom(senderEmail);
        }
        helper.setSubject(subject);
        helper.setText(html, true);
        mailSender.send(message);
    }

    private String render(String title, SubscriberDocument subscriber, List<JobDocument> jobs) {
        StringBuilder html = new StringBuilder("<html><body><h2>").append(title).append("</h2>")
                .append("<p>Xin chào ").append(value(subscriber.getName(), "bạn")).append(",</p>")
                .append("<p>Danh sách việc làm cập nhật ngày ").append(DATE_FORMAT.format(java.time.Instant.now())).append(":</p><ul>");
        for (JobDocument job : jobs) {
            html.append("<li><b>").append(value(job.getName(), "Vị trí tuyển dụng")).append("</b>")
                    .append(" - ").append(value(job.getLocation(), "Không xác định"))
                    .append(" - ").append(job.getSalary() == null ? "Thỏa thuận" : formatSalary(job.getSalary()))
                    .append("</li>");
        }
        return html.append("</ul></body></html>").toString();
    }

    private String value(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private String formatSalary(double salary) {
        return NumberFormat.getNumberInstance(new Locale("vi", "VN")).format(salary) + " VND";
    }
}
