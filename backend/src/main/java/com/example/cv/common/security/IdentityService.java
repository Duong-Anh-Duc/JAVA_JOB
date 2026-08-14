package com.example.cv.common.security;

import com.example.cv.common.model.PermissionView;
import com.example.cv.common.model.RoleRef;
import com.example.cv.common.security.CurrentUser;
import com.example.cv.permission.document.PermissionDocument;
import com.example.cv.permission.repository.PermissionRepository;
import com.example.cv.role.document.RoleDocument;
import com.example.cv.role.repository.RoleRepository;
import com.example.cv.user.document.UserDocument;
import com.example.cv.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IdentityService {
    public static final String ADMIN_ROLE = "SUPER_ADMIN";
    public static final String USER_ROLE = "NORMAL_USER";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public IdentityService(UserRepository userRepository, RoleRepository roleRepository,
                           PermissionRepository permissionRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public CurrentUser current(UserDocument user) {
        RoleDocument role = user.getRole() == null ? null
                : roleRepository.findByIdAndIsDeletedFalse(user.getRole()).orElse(null);
        RoleRef roleRef = role == null ? new RoleRef(null, null) : new RoleRef(role.getId(), role.getName());
        List<PermissionView> permissions = role == null || role.getPermissions() == null ? List.of()
                : permissionRepository.findAllById(role.getPermissions()).stream()
                .filter(permission -> !permission.isDeleted())
                .map(this::permissionView)
                .toList();
        return new CurrentUser(user.getId(), user.getName(), user.getEmail(), roleRef, permissions);
    }

    public RoleRef roleRef(String roleId) {
        return roleRepository.findByIdAndIsDeletedFalse(roleId)
                .map(role -> new RoleRef(role.getId(), role.getName()))
                .orElse(new RoleRef(roleId, null));
    }

    public List<PermissionView> permissionViews(RoleDocument role) {
        if (role == null || role.getPermissions() == null) {
            return List.of();
        }
        return permissionRepository.findAllById(role.getPermissions()).stream()
                .filter(permission -> !permission.isDeleted())
                .map(this::permissionView)
                .toList();
    }

    public PermissionView permissionView(PermissionDocument permission) {
        return new PermissionView(permission.getId(), permission.getName(), permission.getApiPath(),
                permission.getMethod(), permission.getModule());
    }

    public RoleDocument requiredRole(String id) {
        return roleRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> com.example.cv.common.api.ApiException.badRequest("Role không tồn tại"));
    }

    public UserRepository users() {
        return userRepository;
    }
}
