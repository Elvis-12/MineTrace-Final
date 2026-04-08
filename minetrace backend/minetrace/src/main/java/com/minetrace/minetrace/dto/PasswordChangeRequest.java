package com.minetrace.minetrace.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordChangeRequest {
    @NotBlank
    private String currentPassword;

    @NotBlank
    private String newPassword;

    private String confirmPassword;
}
