package com.example.cv.auth.controller;

import com.example.cv.common.api.PublicEndpoint;
import com.example.cv.common.api.ResponseMessage;
import com.example.cv.common.security.CurrentUser;
import com.example.cv.common.security.SecurityUtils;
import com.example.cv.auth.dto.AuthRequests;
import com.example.cv.auth.service.AuthService;
import com.example.cv.user.dto.UserRequests;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @PublicEndpoint
    @ResponseMessage("Login success")
    public Map<String, Object> login(@Valid @RequestBody AuthRequests.LoginRequest request,
                                     HttpServletResponse response) {
        return authService.login(request, response);
    }

    @PostMapping("/register")
    @PublicEndpoint
    @ResponseMessage("Register a new user")
    public Map<String, Object> register(@Valid @RequestBody UserRequests.RegisterRequest request) {
        return authService.register(request);
    }

    @GetMapping("/account")
    @ResponseMessage("Get user information")
    public Map<String, Object> account() {
        return authService.account(SecurityUtils.currentUser());
    }

    @GetMapping("/refresh")
    @PublicEndpoint
    @ResponseMessage("Get user by refresh token")
    public Map<String, Object> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refresh_token".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        return authService.refresh(refreshToken, response);
    }

    @PostMapping("/logout")
    @ResponseMessage("Logout User")
    public String logout(HttpServletResponse response) {
        return authService.logout(SecurityUtils.currentUser(), response);
    }
}
