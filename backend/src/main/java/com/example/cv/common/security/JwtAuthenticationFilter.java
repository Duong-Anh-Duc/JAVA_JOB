package com.example.cv.common.security;

import com.example.cv.common.model.PermissionView;
import com.example.cv.common.model.RoleRef;
import com.example.cv.permission.document.PermissionDocument;
import com.example.cv.permission.repository.PermissionRepository;
import com.example.cv.role.document.RoleDocument;
import com.example.cv.role.repository.RoleRepository;
import com.example.cv.user.document.UserDocument;
import com.example.cv.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public JwtAuthenticationFilter(TokenService tokenService, UserRepository userRepository,
                                   RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Jwt jwt = tokenService.decodeAccess(header.substring(7));
            String userId = jwt.getClaimAsString("_id");
            if (userId == null) {
                userId = jwt.getSubject();
            }
            UserDocument document = userRepository.findById(userId).orElse(null);
            if (document != null && !document.isDeleted()) {
                RoleRef role = resolveRole(document.getRole(), jwt);
                List<PermissionView> permissions = permissionsFor(role.getId());
                CurrentUser currentUser = new CurrentUser(document.getId(), document.getName(),
                        document.getEmail(), role, permissions);
                var authentication = new UsernamePasswordAuthenticationToken(currentUser, header.substring(7), List.of());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (JwtException | IllegalArgumentException ignored) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private RoleRef resolveRole(String roleId, Jwt jwt) {
        if (roleId != null) {
            RoleDocument role = roleRepository.findByIdAndIsDeletedFalse(roleId).orElse(null);
            if (role != null) {
                return new RoleRef(role.getId(), role.getName());
            }
        }
        return TokenService.roleFromClaim(jwt);
    }

    private List<PermissionView> permissionsFor(String roleId) {
        if (roleId == null) {
            return List.of();
        }
        RoleDocument role = roleRepository.findByIdAndIsDeletedFalse(roleId).orElse(null);
        if (role == null || role.getPermissions() == null) {
            return List.of();
        }
        return permissionRepository.findAllById(role.getPermissions()).stream()
                .filter(permission -> !permission.isDeleted())
                .map(this::toView)
                .toList();
    }

    private PermissionView toView(PermissionDocument permission) {
        return new PermissionView(permission.getId(), permission.getName(), permission.getApiPath(),
                permission.getMethod(), permission.getModule());
    }
}
