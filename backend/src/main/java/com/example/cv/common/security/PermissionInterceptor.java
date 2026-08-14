package com.example.cv.common.security;

import com.example.cv.common.api.PublicEndpoint;
import com.example.cv.common.api.SkipPermission;
import com.example.cv.common.model.PermissionView;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class PermissionInterceptor implements HandlerInterceptor {
    private static final String ADMIN_ROLE = "SUPER_ADMIN";
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod method)) {
            return true;
        }
        if (method.hasMethodAnnotation(PublicEndpoint.class)
                || method.getBeanType().isAnnotationPresent(PublicEndpoint.class)
                || method.hasMethodAnnotation(SkipPermission.class)
                || method.getBeanType().isAnnotationPresent(SkipPermission.class)) {
            return true;
        }

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CurrentUser user)) {
            return true; // Spring Security returns 401 before a protected handler can execute.
        }
        if (user.role() != null && ADMIN_ROLE.equals(user.role().getName())) {
            return true;
        }

        String requestPath = request.getRequestURI();
        boolean allowed = user.permissions().stream().anyMatch(permission -> matches(permission, request.getMethod(), requestPath));
        if (!allowed) {
            throw new SecurityException("Bạn không có quyền truy cập vào " + request.getMethod() + " " + requestPath);
        }
        return true;
    }

    private boolean matches(PermissionView permission, String method, String requestPath) {
        if (permission == null || permission.getMethod() == null || permission.getApiPath() == null
                || !permission.getMethod().equalsIgnoreCase(method)) {
            return false;
        }
        String pattern = permission.getApiPath().replaceAll(":([a-zA-Z0-9_-]+)", "*");
        return pathMatcher.match(pattern, requestPath) || pathMatcher.match(pattern, requestPath.split("\\?", 2)[0]);
    }
}
