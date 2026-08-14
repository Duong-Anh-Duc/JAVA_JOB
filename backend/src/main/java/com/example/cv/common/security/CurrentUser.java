package com.example.cv.common.security;

import com.example.cv.common.model.PermissionView;
import com.example.cv.common.model.RoleRef;

import java.util.List;

public record CurrentUser(String id, String name, String email, RoleRef role,
                          List<PermissionView> permissions) {
}
