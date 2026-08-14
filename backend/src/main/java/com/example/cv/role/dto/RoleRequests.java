package com.example.cv.role.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public final class RoleRequests {
    private RoleRequests() {
    }

    public record CreateRequest(@NotBlank String name, @NotBlank String description,
                                Boolean isActive, List<String> permissions) {
    }

    public record UpdateRequest(String name, String description, Boolean isActive, List<String> permissions) {
    }
}
