package com.example.cv.auth.service;

import com.example.cv.analytics.dto.AnalyticsRequests;
import com.example.cv.analytics.service.AnalyticsService;
import com.example.cv.common.api.ApiException;
import com.example.cv.common.security.CurrentUser;
import com.example.cv.common.security.SecurityUtils;
import com.example.cv.common.security.TokenService;
import com.example.cv.common.security.IdentityService;
import com.example.cv.user.document.UserDocument;
import com.example.cv.user.dto.UserRequests;
import com.example.cv.user.service.UserService;
import com.example.cv.auth.dto.AuthRequests;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AuthService {
    private final UserService userService;
    private final IdentityService identityService;
    private final TokenService tokenService;
    private final AnalyticsService analyticsService;
    private final long refreshExpireMs;

    public AuthService(UserService userService, IdentityService identityService, TokenService tokenService,
                       AnalyticsService analyticsService,
                       @org.springframework.beans.factory.annotation.Value("${app.jwt.refresh-expire-ms}") long refreshExpireMs) {
        this.userService = userService;
        this.identityService = identityService;
        this.tokenService = tokenService;
        this.analyticsService = analyticsService;
        this.refreshExpireMs = refreshExpireMs;
    }

    public Map<String, Object> login(AuthRequests.LoginRequest request, HttpServletResponse response) {
        UserDocument document = userService.findByEmail(request.username());
        if (document == null || document.getPassword() == null || !userService.validPassword(request.password(), document.getPassword())) {
            throw ApiException.badRequest("Username/password không hợp lệ");
        }
        CurrentUser user = identityService.current(document);
        String refreshToken = tokenService.refreshToken(user);
        userService.updateRefreshToken(refreshToken, document.getId());
        addRefreshCookie(response, refreshToken);
        analyticsService.trackEvent(new AnalyticsRequests.CreateRequest("USER_LOGIN", null, document.getId(),
                null, null, null, Map.of("email", document.getEmail(), "name", document.getName(),
                "role", user.role() == null ? "" : user.role().getName())));
        return tokens(user, refreshToken);
    }

    public Map<String, Object> register(UserRequests.RegisterRequest request) {
        UserDocument user = userService.register(request);
        return Map.of("_id", user.getId(), "createdAt", user.getCreatedAt());
    }

    public Map<String, Object> refresh(String refreshToken, HttpServletResponse response) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw ApiException.badRequest("Refresh token không hợp lệ. Vui lòng login");
        }
        try {
            tokenService.decodeRefresh(refreshToken);
            UserDocument document = userService.findByRefreshToken(refreshToken);
            if (document == null) {
                throw ApiException.badRequest("Refresh token không hợp lệ. Vui lòng login");
            }
            CurrentUser user = identityService.current(document);
            String replacement = tokenService.refreshToken(user);
            userService.updateRefreshToken(replacement, document.getId());
            addRefreshCookie(response, replacement);
            return tokens(user, replacement);
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            throw ApiException.badRequest("Refresh token không hợp lệ. Vui lòng login");
        }
    }

    public Map<String, Object> account(CurrentUser current) {
        UserDocument document = userService.get(current.id());
        Map<String, Object> user = new LinkedHashMap<>(userService.toView(document));
        user.put("permissions", current.permissions());
        return Map.of("user", user);
    }

    public String logout(CurrentUser current, HttpServletResponse response) {
        userService.updateRefreshToken("", current.id());
        response.addHeader("Set-Cookie", ResponseCookie.from("refresh_token", "")
                .httpOnly(true).path("/").maxAge(Duration.ZERO).build().toString());
        return "ok";
    }

    private Map<String, Object> tokens(CurrentUser user, String refreshToken) {
        return Map.of(
                "access_token", tokenService.accessToken(user),
                "refresh_token", refreshToken,
                "user", Map.of("_id", user.id(), "name", user.name(), "email", user.email(),
                        "role", user.role(), "permissions", user.permissions()));
    }

    private void addRefreshCookie(HttpServletResponse response, String token) {
        response.addHeader("Set-Cookie", ResponseCookie.from("refresh_token", token)
                .httpOnly(true).path("/").maxAge(Duration.ofMillis(refreshExpireMs)).build().toString());
    }
}
