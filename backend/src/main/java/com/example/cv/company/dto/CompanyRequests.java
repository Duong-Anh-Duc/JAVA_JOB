package com.example.cv.company.dto;

import jakarta.validation.constraints.NotBlank;

public final class CompanyRequests {
    private CompanyRequests() {
    }

    public record CreateRequest(
            @NotBlank(message = "Name không được để trống") String name,
            @NotBlank(message = "Address không được để trống") String address,
            @NotBlank(message = "Description không được để trống") String description,
            @NotBlank(message = "Logo không được để trống") String logo) {
    }

    public record UpdateRequest(String name, String address, String description, String logo) {
    }
}
