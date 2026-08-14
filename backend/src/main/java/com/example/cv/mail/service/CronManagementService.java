package com.example.cv.mail.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Service
public class CronManagementService {
    private final ThreadPoolTaskScheduler scheduler;
    private final EmailService emailService;
    private final String timezone;
    private final Map<String, CronJobState> jobs = new LinkedHashMap<>();

    public CronManagementService(ThreadPoolTaskScheduler scheduler, EmailService emailService,
                                 @Value("${app.cron.timezone:Asia/Ho_Chi_Minh}") String timezone,
                                 @Value("${app.cron.daily:0 11 * * *}") String daily,
                                 @Value("${app.cron.weekly:0 9 * * 1}") String weekly) {
        this.scheduler = scheduler;
        this.emailService = emailService;
        this.timezone = timezone;
        jobs.put("daily-job-notification", new CronJobState(normalize(daily), emailService::sendDailyJobNotification));
        jobs.put("weekly-job-summary", new CronJobState(normalize(weekly), emailService::sendWeeklyJobSummary));
    }

    @PostConstruct
    public void startDefaultJobs() {
        jobs.values().forEach(this::schedule);
    }

    public synchronized Map<String, Object> getCronJobsStatus() {
        Map<String, Object> status = new LinkedHashMap<>();
        jobs.forEach((name, job) -> status.put(name, Map.of("running", job.isRunning(),
                "nextRun", "managed by scheduler", "lastRun", job.lastRun == null ? "never" : job.lastRun.toString(),
                "schedule", job.schedule)));
        return Map.of("jobs", status, "totalJobs", jobs.size());
    }

    public synchronized Map<String, Object> startCronJob(String jobName) {
        CronJobState job = jobs.get(jobName);
        if (job == null) return failure("Không tìm thấy cronjob " + jobName);
        if (job.isRunning()) return failure("Cronjob " + jobName + " đã đang chạy");
        schedule(job);
        return success("Cronjob " + jobName + " đã được bật");
    }

    public synchronized Map<String, Object> stopCronJob(String jobName) {
        CronJobState job = jobs.get(jobName);
        if (job == null) return failure("Không tìm thấy cronjob " + jobName);
        if (job.future != null) job.future.cancel(false);
        job.future = null;
        return success("Cronjob " + jobName + " đã được tắt");
    }

    public synchronized Map<String, Object> runCronJobNow(String jobName) {
        CronJobState job = jobs.get(jobName);
        if (job == null) return failure("Không tìm thấy cronjob " + jobName);
        job.run();
        return success("Đã chạy cronjob " + jobName + " ngay lập tức");
    }

    public synchronized Map<String, Object> updateCronSchedule(String jobName, String schedule) {
        CronJobState job = jobs.get(jobName);
        if (job == null) return failure("Không tìm thấy cronjob " + jobName);
        boolean wasRunning = job.isRunning();
        if (job.future != null) job.future.cancel(false);
        job.schedule = normalize(schedule);
        job.future = null;
        if (wasRunning) schedule(job);
        return Map.of("success", true, "message", "Đã cập nhật lịch trình cho cronjob " + jobName,
                "newSchedule", schedule);
    }

    private void schedule(CronJobState job) {
        job.future = scheduler.schedule(job::run, new CronTrigger(job.schedule, java.util.TimeZone.getTimeZone(timezone).toZoneId()));
    }

    private String normalize(String schedule) {
        if (schedule == null || schedule.isBlank()) {
            throw new IllegalArgumentException("Cron schedule không được để trống");
        }
        String trimmed = schedule.trim();
        return trimmed.split("\\s+").length == 5 ? "0 " + trimmed : trimmed;
    }

    private Map<String, Object> success(String message) {
        return Map.of("success", true, "message", message);
    }

    private Map<String, Object> failure(String message) {
        return Map.of("success", false, "message", message);
    }

    private static class CronJobState {
        private String schedule;
        private final Runnable task;
        private ScheduledFuture<?> future;
        private Instant lastRun;

        private CronJobState(String schedule, Runnable task) {
            this.schedule = schedule;
            this.task = task;
        }

        private boolean isRunning() {
            return future != null && !future.isCancelled();
        }

        private void run() {
            lastRun = Instant.now();
            task.run();
        }
    }
}
