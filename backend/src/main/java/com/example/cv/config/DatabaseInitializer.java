package com.example.cv.config;

import com.example.cv.common.security.IdentityService;
import com.example.cv.permission.document.PermissionDocument;
import com.example.cv.permission.repository.PermissionRepository;
import com.example.cv.role.document.RoleDocument;
import com.example.cv.role.repository.RoleRepository;
import com.example.cv.user.document.UserDocument;
import com.example.cv.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DatabaseInitializer {
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final boolean enabled;
    private final String initialPassword;

    public DatabaseInitializer(PermissionRepository permissionRepository, RoleRepository roleRepository,
                               UserRepository userRepository, PasswordEncoder passwordEncoder,
                               @Value("${app.init.enabled:false}") boolean enabled,
                               @Value("${app.init.password:123456}") String initialPassword) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.enabled = enabled;
        this.initialPassword = initialPassword;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {
        if (!enabled) {
            return;
        }
        List<PermissionDocument> permissions = permissionRepository.findAllByIsDeletedFalse();
        if (permissions.isEmpty()) {
            permissions = permissionRepository.saveAll(permissionDefinitions());
        }

        RoleDocument admin = roleRepository.findByNameAndIsDeletedFalse(IdentityService.ADMIN_ROLE).orElse(null);
        if (admin == null) {
            RoleDocument role = new RoleDocument();
            role.setName(IdentityService.ADMIN_ROLE);
            role.setDescription("Admin có toàn bộ quyền");
            role.setActive(true);
            role.setPermissions(permissions.stream().map(PermissionDocument::getId).toList());
            admin = roleRepository.save(role);
        }
        RoleDocument userRole = roleRepository.findByNameAndIsDeletedFalse(IdentityService.USER_ROLE).orElse(null);
        if (userRole == null) {
            RoleDocument role = new RoleDocument();
            role.setName(IdentityService.USER_ROLE);
            role.setDescription("Người dùng thông thường");
            role.setActive(true);
            role.setPermissions(List.of());
            userRole = roleRepository.save(role);
        }

        createUserIfMissing("admin@gmail.com", "I'm Admin", "MALE", "Vietnam", admin.getId(), 69);
        createUserIfMissing("ducytcg123456@gmail.com", "Dương Anh Đức", "MALE", "Vietnam", admin.getId(), 96);
        createUserIfMissing("user@gmail.com", "I'm Normal User", "MALE", "Vietnam", userRole.getId(), 65);
    }

    private void createUserIfMissing(String email, String name, String gender, String address,
                                     String role, int age) {
        if (userRepository.findByEmailAndIsDeletedFalse(email).isPresent()) {
            return;
        }
        UserDocument user = new UserDocument();
        user.setEmail(email);
        user.setName(name);
        user.setGender(gender);
        user.setAddress(address);
        user.setAge(age);
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(initialPassword));
        userRepository.save(user);
    }

    private List<PermissionDocument> permissionDefinitions() {
        List<PermissionDocument> result = new ArrayList<>();
        addModule(result, "USERS", new String[][]{
                {"Get Users", "/api/v1/users", "GET"}, {"Get User by ID", "/api/v1/users/:id", "GET"},
                {"Create User", "/api/v1/users", "POST"}, {"Update User", "/api/v1/users/:id", "PATCH"},
                {"Delete User", "/api/v1/users/:id", "DELETE"}});
        addModule(result, "AUTH", new String[][]{{"Get My Account", "/api/v1/auth/account", "GET"}});
        addModule(result, "COMPANIES", new String[][]{
                {"Get Companies", "/api/v1/companies", "GET"}, {"Get Company by ID", "/api/v1/companies/:id", "GET"},
                {"Create Company", "/api/v1/companies", "POST"}, {"Update Company", "/api/v1/companies/:id", "PATCH"},
                {"Delete Company", "/api/v1/companies/:id", "DELETE"}});
        addModule(result, "JOBS", new String[][]{
                {"Get Jobs", "/api/v1/jobs", "GET"}, {"Get Job by ID", "/api/v1/jobs/:id", "GET"},
                {"Create Job", "/api/v1/jobs", "POST"}, {"Update Job", "/api/v1/jobs/:id", "PATCH"},
                {"Delete Job", "/api/v1/jobs/:id", "DELETE"}});
        addModule(result, "ROLES", new String[][]{
                {"Get Roles", "/api/v1/roles", "GET"}, {"Get Role by ID", "/api/v1/roles/:id", "GET"},
                {"Create Role", "/api/v1/roles", "POST"}, {"Update Role", "/api/v1/roles/:id", "PATCH"},
                {"Delete Role", "/api/v1/roles/:id", "DELETE"}});
        addModule(result, "PERMISSIONS", new String[][]{
                {"Get Permissions", "/api/v1/permissions", "GET"}, {"Get Permission by ID", "/api/v1/permissions/:id", "GET"},
                {"Create Permission", "/api/v1/permissions", "POST"}, {"Update Permission", "/api/v1/permissions/:id", "PATCH"},
                {"Delete Permission", "/api/v1/permissions/:id", "DELETE"}});
        addModule(result, "RESUMES", new String[][]{
                {"Get Resumes", "/api/v1/resumes", "GET"}, {"Get Resume by ID", "/api/v1/resumes/:id", "GET"},
                {"Create Resume", "/api/v1/resumes", "POST"}, {"Update Resume", "/api/v1/resumes/:id", "PATCH"},
                {"Delete Resume", "/api/v1/resumes/:id", "DELETE"}, {"Get Resume by User", "/api/v1/resumes/by-user", "POST"}});
        addModule(result, "ANALYTICS", new String[][]{
                {"Get Analytics", "/api/v1/analytics", "GET"}, {"Get Analytics Dashboard", "/api/v1/analytics/dashboard", "GET"},
                {"Get Analytics Events", "/api/v1/analytics/stats/events", "GET"}, {"Get Analytics Daily", "/api/v1/analytics/stats/daily", "GET"},
                {"Get Analytics Users", "/api/v1/analytics/stats/users", "GET"}, {"Delete Analytics", "/api/v1/analytics/:id", "DELETE"}});
        return result;
    }

    private void addModule(List<PermissionDocument> result, String module, String[][] definitions) {
        for (String[] definition : definitions) {
            PermissionDocument permission = new PermissionDocument();
            permission.setName(definition[0]);
            permission.setApiPath(definition[1]);
            permission.setMethod(definition[2]);
            permission.setModule(module);
            result.add(permission);
        }
    }
}
