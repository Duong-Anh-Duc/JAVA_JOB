package com.example.cv.job.dto;

import com.example.cv.common.model.CompanySnapshot;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;

public final class JobRequests {
    private JobRequests() {
    }

    public record CreateRequest(
            @NotBlank(message = "Name không được để trống") String name,
            @NotEmpty(message = "Skills không được để trống") List<String> skills,
            @NotNull @Valid CompanySnapshot company,
            @NotNull @Min(value = 0, message = "Lương phải >= 0") Double salary,
            @NotNull @Min(value = 1, message = "Số lượng phải >= 1") Integer quantity,
            @NotBlank String location,
            @NotBlank String level,
            String description,
            @NotNull Instant startDate,
            @NotNull Instant endDate,
            Boolean isActive) {
    }

    public record UpdateRequest(String name, List<String> skills, CompanySnapshot company, Double salary,
                                Integer quantity, String location, String level, String description,
                                Instant startDate, Instant endDate, Boolean isActive) {
    }
}
