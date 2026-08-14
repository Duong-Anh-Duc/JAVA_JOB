package com.example.cv.permission.service;

import com.example.cv.common.api.ApiException;
import com.example.cv.common.model.AuditInfo;
import com.example.cv.common.security.CurrentUser;
import com.example.cv.common.service.PaginationService;
import com.example.cv.permission.document.PermissionDocument;
import com.example.cv.permission.dto.PermissionRequests;
import com.example.cv.permission.repository.PermissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PermissionService {
    private final PermissionRepository repository;

    public PermissionService(PermissionRepository repository) {
        this.repository = repository;
    }

    public Map<String, Object> create(PermissionRequests.CreateRequest request, CurrentUser actor) {
        boolean exists = repository.findAllByIsDeletedFalse().stream()
                .anyMatch(item -> (request.apiPath() + request.method()).equals(item.getApiPath() + item.getMethod()));
        if (exists) {
            throw ApiException.badRequest("Api " + request.apiPath() + " + Method " + request.method() + " đã tồn tại");
        }
        PermissionDocument permission = new PermissionDocument();
        permission.setName(request.name());
        permission.setApiPath(request.apiPath());
        permission.setMethod(request.method());
        permission.setModule(request.module());
        permission.setCreatedBy(audit(actor));
        PermissionDocument saved = repository.save(permission);
        return Map.of("_id", saved.getId(), "createdAt", saved.getCreatedAt());
    }

    public Map<String, Object> findAll(Integer current, Integer pageSize) {
        var page = PaginationService.page(repository.findAllByIsDeletedFalse(), current, pageSize, null);
        return page.asMap();
    }

    public PermissionDocument get(String id) {
        return repository.findById(id).filter(item -> !item.isDeleted())
                .orElseThrow(() -> ApiException.notFound("id không hợp lệ"));
    }

    public PermissionDocument update(String id, PermissionRequests.UpdateRequest request, CurrentUser actor) {
        PermissionDocument permission = get(id);
        if (request.name() != null) permission.setName(request.name());
        if (request.apiPath() != null) permission.setApiPath(request.apiPath());
        if (request.method() != null) permission.setMethod(request.method());
        if (request.module() != null) permission.setModule(request.module());
        permission.setUpdatedBy(audit(actor));
        return repository.save(permission);
    }

    public Map<String, Object> remove(String id, CurrentUser actor) {
        PermissionDocument permission = get(id);
        permission.setDeletedBy(audit(actor));
        permission.setDeleted(true);
        repository.save(permission);
        return Map.of("acknowledged", true, "deletedCount", 1);
    }

    private AuditInfo audit(CurrentUser actor) {
        return actor == null ? null : new AuditInfo(actor.id(), actor.email());
    }
}
