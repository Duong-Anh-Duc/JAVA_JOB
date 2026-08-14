package com.example.cv.auth.dto;

import jakarta.validation.constraints.NotBlank;

public final class AuthRequests {
    private AuthRequests() {
    }

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {
    }
}
