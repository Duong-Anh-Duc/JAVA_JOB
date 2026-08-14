package com.example.cv.permission.dto;

import jakarta.validation.constraints.NotBlank;

public final class PermissionRequests {
    private PermissionRequests() {
    }

    public record CreateRequest(@NotBlank String name, @NotBlank String apiPath,
                                @NotBlank String method, @NotBlank String module) {
    }

    public record UpdateRequest(String name, String apiPath, String method, String module) {
    }
}
