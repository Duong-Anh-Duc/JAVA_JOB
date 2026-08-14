package com.example.cv.subscriber.service;

import com.example.cv.common.api.ApiException;
import com.example.cv.common.model.AuditInfo;
import com.example.cv.common.security.CurrentUser;
import com.example.cv.common.service.PaginationService;
import com.example.cv.subscriber.document.SubscriberDocument;
import com.example.cv.subscriber.dto.SubscriberRequests;
import com.example.cv.subscriber.repository.SubscriberRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SubscriberService {
    private final SubscriberRepository repository;

    public SubscriberService(SubscriberRepository repository) {
        this.repository = repository;
    }

    public Map<String, Object> create(SubscriberRequests.CreateRequest request, CurrentUser actor) {
        if (repository.findByEmailAndIsDeletedFalse(request.email()).isPresent()) {
            throw ApiException.badRequest("Email " + request.email() + " đã tồn tại! Vui lòng sử dụng email khác.");
        }
        SubscriberDocument subscriber = new SubscriberDocument();
        subscriber.setEmail(request.email());
        subscriber.setName(request.name());
        subscriber.setSkills(request.skills());
        subscriber.setCreatedBy(audit(actor));
        SubscriberDocument saved = repository.save(subscriber);
        return Map.of("_id", saved.getId(), "createdBy", saved.getCreatedBy());
    }

    public Map<String, Object> findAll(Integer current, Integer pageSize) {
        var page = PaginationService.page(repository.findAllByIsDeletedFalse(), current, pageSize, null);
        return page.asMap();
    }

    public SubscriberDocument findOne(String id) {
        return repository.findById(id).filter(item -> !item.isDeleted())
                .orElseThrow(() -> ApiException.notFound("Không tìm thấy người đăng ký"));
    }

    public Map<String, Object> skills(CurrentUser actor) {
        SubscriberDocument subscriber = repository.findByEmailAndIsDeletedFalse(actor.email())
                .orElseThrow(() -> ApiException.notFound("Không tìm thấy người đăng ký"));
        return Map.of("_id", subscriber.getId(), "skills", subscriber.getSkills());
    }

    public SubscriberDocument update(String id, SubscriberRequests.UpdateRequest request, CurrentUser actor) {
        SubscriberDocument subscriber = findOne(id);
        if (request.name() != null) subscriber.setName(request.name());
        if (request.skills() != null) subscriber.setSkills(request.skills());
        subscriber.setUpdatedBy(audit(actor));
        return repository.save(subscriber);
    }

    public SubscriberDocument upsert(SubscriberRequests.UpsertRequest request, CurrentUser actor) {
        SubscriberDocument subscriber = repository.findByEmailAndIsDeletedFalse(request.email()).orElseGet(SubscriberDocument::new);
        subscriber.setEmail(request.email());
        if (request.name() != null) subscriber.setName(request.name());
        if (request.skills() != null) subscriber.setSkills(request.skills());
        subscriber.setUpdatedBy(audit(actor));
        return repository.save(subscriber);
    }

    public Map<String, Object> remove(String id, CurrentUser actor) {
        SubscriberDocument subscriber = findOne(id);
        subscriber.setDeletedBy(audit(actor));
        subscriber.setDeleted(true);
        repository.save(subscriber);
        return Map.of("acknowledged", true, "deletedCount", 1);
    }

    private AuditInfo audit(CurrentUser actor) {
        return actor == null ? null : new AuditInfo(actor.id(), actor.email());
    }
}
