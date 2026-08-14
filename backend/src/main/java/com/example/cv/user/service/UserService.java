package com.example.cv.user.service;

import com.example.cv.analytics.dto.AnalyticsRequests;
import com.example.cv.analytics.service.AnalyticsService;
import com.example.cv.common.api.ApiException;
import com.example.cv.common.model.AuditInfo;
import com.example.cv.common.security.CurrentUser;
import com.example.cv.common.security.IdentityService;
import com.example.cv.common.service.PaginationService;
import com.example.cv.role.document.RoleDocument;
import com.example.cv.user.document.UserDocument;
import com.example.cv.user.dto.UserRequests;
import com.example.cv.role.repository.RoleRepository;
import com.example.cv.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final IdentityService identityService;
    private final AnalyticsService analyticsService;

    public UserService(UserRepository userRepository, RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder, IdentityService identityService,
                       AnalyticsService analyticsService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.identityService = identityService;
        this.analyticsService = analyticsService;
    }

    public UserDocument create(UserRequests.CreateRequest request, CurrentUser actor) {
        ensureEmailAvailable(request.email());
        identityService.requiredRole(request.role());
        UserDocument user = new UserDocument();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setName(request.name());
        user.setGender(request.gender());
        user.setAddress(request.address());
        user.setRole(request.role());
        user.setCompany(request.company());
        UserDocument saved = userRepository.save(user);
        track("USER_CREATED", saved.getId(), actor, Map.of("email", saved.getEmail(), "name", saved.getName(), "role", saved.getRole()));
        return saved;
    }

    public UserDocument register(UserRequests.RegisterRequest request) {
        ensureEmailAvailable(request.email());
        RoleDocument role = roleRepository.findByNameAndIsDeletedFalse(IdentityService.USER_ROLE)
                .orElseThrow(() -> ApiException.badRequest("Chưa có role NORMAL_USER trong database"));
        UserDocument user = new UserDocument();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setName(request.name());
        user.setGender(request.gender());
        user.setAddress(request.address());
        user.setRole(role.getId());
        UserDocument saved = userRepository.save(user);
        track("USER_REGISTERED", saved.getId(), null, Map.of("email", saved.getEmail(), "name", saved.getName()));
        return saved;
    }

    public UserDocument update(String id, UserRequests.UpdateRequest request, CurrentUser actor) {
        UserDocument user = get(id);
        if (request.email() != null && !request.email().equals(user.getEmail())) {
            ensureEmailAvailable(request.email());
            user.setEmail(request.email());
        }
        if (request.name() != null) user.setName(request.name());
        if (request.gender() != null) user.setGender(request.gender());
        if (request.address() != null) user.setAddress(request.address());
        if (request.age() != null) user.setAge(request.age());
        if (request.company() != null) user.setCompany(request.company());
        if (request.role() != null) {
            identityService.requiredRole(request.role());
            user.setRole(request.role());
        }
        user.setUpdatedBy(audit(actor));
        UserDocument saved = userRepository.save(user);
        track("USER_UPDATED", saved.getId(), actor, Map.of("updatedFields", List.of("profile")));
        return saved;
    }

    public Map<String, Object> findAll(Integer current, Integer pageSize, String email, String name, String gender) {
        List<UserDocument> users = userRepository.findAllByIsDeletedFalse();
        var page = PaginationService.page(users, current, pageSize, user ->
                (email == null || email.isBlank() || user.getEmail().toLowerCase().contains(email.toLowerCase()))
                        && (name == null || name.isBlank() || (user.getName() != null && user.getName().toLowerCase().contains(name.toLowerCase())))
                        && (gender == null || gender.isBlank() || gender.equalsIgnoreCase(user.getGender())));
        return page.asMapWith(page.result().stream().map(this::toView).toList());
    }

    public Map<String, Object> findOneView(String id) {
        return toView(get(id));
    }

    public UserDocument get(String id) {
        return userRepository.findById(id).filter(user -> !user.isDeleted())
                .orElseThrow(() -> ApiException.notFound("not found user"));
    }

    public UserDocument findByEmail(String email) {
        return userRepository.findByEmailAndIsDeletedFalse(email).orElse(null);
    }

    public UserDocument findByRefreshToken(String refreshToken) {
        return userRepository.findByRefreshTokenAndIsDeletedFalse(refreshToken).orElse(null);
    }

    public boolean validPassword(String raw, String encoded) {
        return passwordEncoder.matches(raw, encoded);
    }

    public void updateRefreshToken(String token, String id) {
        UserDocument user = get(id);
        user.setRefreshToken(token);
        userRepository.save(user);
    }

    public Map<String, Object> remove(String id, CurrentUser actor) {
        UserDocument user = get(id);
        if ("admin@gmail.com".equalsIgnoreCase(user.getEmail())) {
            throw ApiException.badRequest("Không thể xoá tài khoản admin");
        }
        user.setDeletedBy(audit(actor));
        user.setDeleted(true);
        userRepository.save(user);
        return Map.of("acknowledged", true, "deletedCount", 1);
    }

    public Map<String, Object> toView(UserDocument user) {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("_id", user.getId());
        result.put("email", user.getEmail());
        result.put("name", user.getName());
        result.put("age", user.getAge());
        result.put("gender", user.getGender());
        result.put("address", user.getAddress());
        result.put("company", user.getCompany());
        result.put("role", identityService.roleRef(user.getRole()));
        result.put("createdAt", user.getCreatedAt());
        result.put("updatedAt", user.getUpdatedAt());
        return result;
    }

    public AuditInfo audit(CurrentUser actor) {
        return actor == null ? null : new AuditInfo(actor.id(), actor.email());
    }

    private void ensureEmailAvailable(String email) {
        if (userRepository.findByEmailAndIsDeletedFalse(email).isPresent()) {
            throw ApiException.badRequest("Email : " + email + " đã tồn tại trên hệ thống!");
        }
    }

    private void track(String event, String resourceId, CurrentUser actor, Map<String, Object> metadata) {
        analyticsService.trackEvent(new AnalyticsRequests.CreateRequest(event, resourceId,
                actor == null ? null : actor.id(), null, null, null, metadata));
    }
}
