package com.example.cv.mail.controller;

import com.example.cv.common.api.PublicEndpoint;
import com.example.cv.common.api.ResponseMessage;
import com.example.cv.mail.service.CronManagementService;
import com.example.cv.mail.service.EmailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/email")
@PublicEndpoint
public class EmailController {
    private final EmailService emailService;
    private final CronManagementService cronManagementService;

    public EmailController(EmailService emailService, CronManagementService cronManagementService) {
        this.emailService = emailService;
        this.cronManagementService = cronManagementService;
    }

    @GetMapping
    @ResponseMessage("Send email successfully")
    public Map<String, Object> sendEmail() {
        return emailService.sendJobNotificationManually();
    }

    @GetMapping("/cron-test")
    @ResponseMessage("Test cronjob gửi email thành công")
    public Map<String, Object> testCron() {
        return emailService.sendJobNotificationManually();
    }

    @GetMapping("/cron-status")
    @ResponseMessage("Lấy trạng thái cronjobs thành công")
    public Map<String, Object> status() {
        return cronManagementService.getCronJobsStatus();
    }

    @PostMapping("/cron-start/{jobName}")
    @ResponseMessage("Bật cronjob thành công")
    public Map<String, Object> start(@PathVariable String jobName) {
        return cronManagementService.startCronJob(jobName);
    }

    @PostMapping("/cron-stop/{jobName}")
    @ResponseMessage("Tắt cronjob thành công")
    public Map<String, Object> stop(@PathVariable String jobName) {
        return cronManagementService.stopCronJob(jobName);
    }

    @PostMapping("/cron-run/{jobName}")
    @ResponseMessage("Chạy cronjob ngay thành công")
    public Map<String, Object> run(@PathVariable String jobName) {
        return cronManagementService.runCronJobNow(jobName);
    }

    @PostMapping("/cron-schedule/{jobName}")
    @ResponseMessage("Cập nhật lịch trình cronjob thành công")
    public Map<String, Object> schedule(@PathVariable String jobName, @RequestBody Map<String, String> body) {
        return cronManagementService.updateCronSchedule(jobName, body.get("schedule"));
    }
}
