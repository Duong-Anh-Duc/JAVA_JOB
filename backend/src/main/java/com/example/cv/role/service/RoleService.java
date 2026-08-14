package com.example.cv.role.service;

import com.example.cv.common.api.ApiException;
import com.example.cv.common.model.AuditInfo;
import com.example.cv.common.security.CurrentUser;
import com.example.cv.common.security.IdentityService;
import com.example.cv.common.service.PaginationService;
import com.example.cv.role.document.RoleDocument;
import com.example.cv.role.dto.RoleRequests;
import com.example.cv.role.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final IdentityService identityService;

    public RoleService(RoleRepository roleRepository, IdentityService identityService) {
        this.roleRepository = roleRepository;
        this.identityService = identityService;
    }

    public Map<String, Object> create(RoleRequests.CreateRequest request, CurrentUser actor) {
        if (roleRepository.findByNameAndIsDeletedFalse(request.name()).isPresent()) {
            throw ApiException.badRequest("Name đã tồn tại vui lòng chọn lại");
        }
        RoleDocument role = new RoleDocument();
        role.setName(request.name());
        role.setDescription(request.description());
        role.setActive(Boolean.TRUE.equals(request.isActive()));
        role.setPermissions(request.permissions() == null ? List.of() : request.permissions());
        role.setCreatedBy(audit(actor));
        RoleDocument saved = roleRepository.save(role);
        return Map.of("_id", saved.getId(), "createdAt", saved.getCreatedAt());
    }

    public RoleDocument update(String id, RoleRequests.UpdateRequest request, CurrentUser actor) {
        RoleDocument role = get(id);
        if (request.name() != null) role.setName(request.name());
        if (request.description() != null) role.setDescription(request.description());
        if (request.isActive() != null) role.setActive(request.isActive());
        if (request.permissions() != null) role.setPermissions(request.permissions());
        role.setUpdatedBy(audit(actor));
        return roleRepository.save(role);
    }

    public Map<String, Object> findAll(Integer current, Integer pageSize) {
        var page = PaginationService.page(roleRepository.findAllByIsDeletedFalse(), current, pageSize, null);
        return page.asMapWith(page.result().stream().map(this::toView).toList());
    }

    public Map<String, Object> findOne(String id) {
        return toView(get(id));
    }

    public Map<String, Object> remove(String id, CurrentUser actor) {
        RoleDocument role = get(id);
        if (IdentityService.ADMIN_ROLE.equals(role.getName())) {
            throw ApiException.badRequest("Không thể xoá role admin");
        }
        role.setDeletedBy(audit(actor));
        role.setDeleted(true);
        roleRepository.save(role);
        return Map.of("acknowledged", true, "deletedCount", 1);
    }

    public RoleDocument get(String id) {
        return roleRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> ApiException.notFound("not found role"));
    }

    private Map<String, Object> toView(RoleDocument role) {
        LinkedHashMap<String, Object> view = new LinkedHashMap<>();
        view.put("_id", role.getId());
        view.put("name", role.getName());
        view.put("description", role.getDescription());
        view.put("isActive", role.isActive());
        view.put("permissions", identityService.permissionViews(role));
        view.put("createdAt", role.getCreatedAt());
        view.put("updatedAt", role.getUpdatedAt());
        return view;
    }

    private AuditInfo audit(CurrentUser actor) {
        return actor == null ? null : new AuditInfo(actor.id(), actor.email());
    }
}
