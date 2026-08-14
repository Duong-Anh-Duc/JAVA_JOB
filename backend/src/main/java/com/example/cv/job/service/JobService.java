package com.example.cv.job.service;

import com.example.cv.analytics.dto.AnalyticsRequests;
import com.example.cv.analytics.service.AnalyticsService;
import com.example.cv.common.model.AuditInfo;
import com.example.cv.common.security.CurrentUser;
import com.example.cv.common.service.PaginationService;
import com.example.cv.job.document.JobDocument;
import com.example.cv.job.dto.JobRequests;
import com.example.cv.job.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class JobService {
    private final JobRepository repository;
    private final AnalyticsService analyticsService;

    public JobService(JobRepository repository, AnalyticsService analyticsService) {
        this.repository = repository;
        this.analyticsService = analyticsService;
    }

    public Map<String, Object> create(JobRequests.CreateRequest request, CurrentUser actor) {
        JobDocument job = new JobDocument();
        copy(job, request);
        job.setCreatedBy(audit(actor));
        JobDocument saved = repository.save(job);
        track("JOB_CREATED", saved, actor, Map.of("name", saved.getName(), "company", saved.getCompany(),
                "location", saved.getLocation(), "salary", saved.getSalary()));
        return Map.of("_id", saved.getId(), "createdAt", saved.getCreatedAt());
    }

    public JobDocument update(String id, JobRequests.UpdateRequest request, CurrentUser actor) {
        JobDocument job = get(id);
        if (request.name() != null) job.setName(request.name());
        if (request.skills() != null) job.setSkills(request.skills());
        if (request.company() != null) job.setCompany(request.company());
        if (request.salary() != null) job.setSalary(request.salary());
        if (request.quantity() != null) job.setQuantity(request.quantity());
        if (request.location() != null) job.setLocation(request.location());
        if (request.level() != null) job.setLevel(request.level());
        if (request.description() != null) job.setDescription(request.description());
        if (request.startDate() != null) job.setStartDate(request.startDate());
        if (request.endDate() != null) job.setEndDate(request.endDate());
        if (request.isActive() != null) job.setActive(request.isActive());
        job.setUpdatedBy(audit(actor));
        JobDocument saved = repository.save(job);
        track("JOB_UPDATED", saved, actor, Map.of("updatedFields", java.util.List.of("job")));
        return saved;
    }

    public Map<String, Object> findAll(Integer current, Integer pageSize, String name, String location, String level) {
        var page = PaginationService.page(repository.findAllByIsDeletedFalse(), current, pageSize, job ->
                (name == null || name.isBlank() || contains(job.getName(), name))
                        && (location == null || location.isBlank() || contains(job.getLocation(), location))
                        && (level == null || level.isBlank() || contains(job.getLevel(), level)));
        return page.asMap();
    }

    public JobDocument findOne(String id, CurrentUser viewer) {
        JobDocument job = get(id);
        track("JOB_VIEWED", job, viewer, Map.of("jobName", job.getName(), "company", job.getCompany()));
        return job;
    }

    public Map<String, Object> remove(String id, CurrentUser actor) {
        JobDocument job = get(id);
        job.setDeletedBy(audit(actor));
        job.setActive(false);
        job.setDeleted(true);
        repository.save(job);
        return Map.of("acknowledged", true, "deletedCount", 1);
    }

    public java.util.List<JobDocument> all(int limit) {
        return repository.findAllByIsDeletedFalse().stream().limit(limit).toList();
    }

    public JobDocument get(String id) {
        return repository.findById(id).filter(job -> !job.isDeleted())
                .orElseThrow(() -> com.example.cv.common.api.ApiException.notFound("Job not found"));
    }

    private void copy(JobDocument job, JobRequests.CreateRequest request) {
        job.setName(request.name());
        job.setSkills(request.skills());
        job.setCompany(request.company());
        job.setSalary(request.salary());
        job.setQuantity(request.quantity());
        job.setLocation(request.location());
        job.setLevel(request.level());
        job.setDescription(request.description());
        job.setStartDate(request.startDate());
        job.setEndDate(request.endDate());
        job.setActive(request.isActive() == null || request.isActive());
    }

    private void track(String event, JobDocument job, CurrentUser actor, Map<String, Object> metadata) {
        analyticsService.trackEvent(new AnalyticsRequests.CreateRequest(event, job.getId(),
                actor == null ? null : actor.id(), null, null, null, metadata));
    }

    private AuditInfo audit(CurrentUser actor) {
        return actor == null ? null : new AuditInfo(actor.id(), actor.email());
    }

    private boolean contains(String value, String search) {
        return value != null && value.toLowerCase().contains(search.toLowerCase());
    }
}
