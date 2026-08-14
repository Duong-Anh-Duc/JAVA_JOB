package com.example.cv.user.dto;

import com.example.cv.common.model.CompanySnapshot;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public final class UserRequests {
    private UserRequests() {
    }

    public record RegisterRequest(
            @NotBlank(message = "Email không được để trống") @Email String email,
            @NotBlank(message = "Password không được để trống") String password,
            @NotBlank(message = "Name không được để trống") String name,
            @NotBlank(message = "Gender không được để trống") String gender,
            @NotBlank(message = "Address không được để trống") String address) {
    }

    public record CreateRequest(
            @NotBlank @Email String email,
            @NotBlank(message = "Password không được để trống") String password,
            @NotBlank String name,
            @NotBlank String gender,
            @NotBlank String address,
            @NotBlank String role,
            @NotNull @Valid CompanySnapshot company) {
    }

    public record UpdateRequest(String id, String email, String name, String gender, String address,
                                String role, CompanySnapshot company, Integer age) {
    }
}
