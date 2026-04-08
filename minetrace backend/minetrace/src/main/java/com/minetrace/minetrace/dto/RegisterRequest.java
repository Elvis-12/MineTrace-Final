package com.minetrace.minetrace.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    private String fullName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    private String confirmPassword;

    @NotBlank
    private String role;

    private String organizationId;
    private String organizationName;
}
