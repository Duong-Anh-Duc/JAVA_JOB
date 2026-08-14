package com.example.cv.resume.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public final class ResumeRequests {
    private ResumeRequests() {
    }

    public record CreateRequest(@NotBlank String url,
                                @NotBlank @Pattern(regexp = "PENDING|REVIEWING|APPROVED|REJECTED") String status,
                                @NotBlank String companyId, @NotBlank String jobId) {
    }

    public record UpdateRequest(@NotBlank @Pattern(regexp = "PENDING|REVIEWING|APPROVED|REJECTED") String status) {
    }
}
