package com.example.cv.subscriber.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public final class SubscriberRequests {
    private SubscriberRequests() {
    }

    public record CreateRequest(@NotBlank @Email String email, @NotBlank String name,
                                @NotEmpty List<@NotBlank String> skills) {
    }

    public record UpdateRequest(String name, List<@NotBlank String> skills) {
    }

    public record UpsertRequest(@NotBlank @Email String email, String name, List<String> skills) {
    }
}
