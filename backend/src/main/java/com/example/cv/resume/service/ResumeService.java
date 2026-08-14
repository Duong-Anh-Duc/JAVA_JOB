package com.example.cv.resume.service;

import com.example.cv.common.api.ApiException;
import com.example.cv.common.model.AuditInfo;
import com.example.cv.common.security.CurrentUser;
import com.example.cv.common.service.PaginationService;
import com.example.cv.resume.document.ResumeDocument;
import com.example.cv.resume.document.ResumeHistory;
import com.example.cv.resume.dto.ResumeRequests;
import com.example.cv.resume.repository.ResumeRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
public class ResumeService {
    private final ResumeRepository repository;

    public ResumeService(ResumeRepository repository) {
        this.repository = repository;
    }

    public Map<String, Object> create(ResumeRequests.CreateRequest request, CurrentUser actor) {
        ResumeDocument resume = new ResumeDocument();
        resume.setEmail(actor.email());
        resume.setUserId(actor.id());
        resume.setUrl(request.url());
        resume.setStatus(request.status() == null ? "PENDING" : request.status());
        resume.setCompanyId(request.companyId());
        resume.setJobId(request.jobId());
        resume.getHistory().add(history(resume.getStatus(), actor));
        resume.setCreatedBy(audit(actor));
        ResumeDocument saved = repository.save(resume);
        return Map.of("_id", saved.getId(), "createdAt", saved.getCreatedAt());
    }

    public Map<String, Object> findAll(Integer current, Integer pageSize) {
        var page = PaginationService.page(repository.findAllByIsDeletedFalse(), current, pageSize, null);
        return page.asMap();
    }

    public ResumeDocument findOne(String id) {
        return repository.findById(id).filter(resume -> !resume.isDeleted())
                .orElseThrow(() -> ApiException.notFound("Resume not found"));
    }

    public java.util.List<ResumeDocument> byUser(CurrentUser actor) {
        return repository.findAllByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(actor.id());
    }

    public ResumeDocument update(String id, ResumeRequests.UpdateRequest request, CurrentUser actor) {
        ResumeDocument resume = findOne(id);
        resume.setStatus(request.status());
        resume.getHistory().add(history(request.status(), actor));
        resume.setUpdatedBy(audit(actor));
        return repository.save(resume);
    }

    public Map<String, Object> remove(String id, CurrentUser actor) {
        ResumeDocument resume = findOne(id);
        resume.setDeleteBy(audit(actor));
        resume.setDeleted(true);
        repository.save(resume);
        return Map.of("acknowledged", true, "deletedCount", 1);
    }

    private ResumeHistory history(String status, CurrentUser actor) {
        return new ResumeHistory(status, Instant.now(), audit(actor));
    }

    private AuditInfo audit(CurrentUser actor) {
        return actor == null ? null : new AuditInfo(actor.id(), actor.email());
    }
}
