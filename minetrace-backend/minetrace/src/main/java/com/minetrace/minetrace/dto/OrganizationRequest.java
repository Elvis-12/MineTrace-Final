package com.minetrace.minetrace.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrganizationRequest {
    @NotBlank
    private String name;
    private String address;
    private String phone;
    private String email;
}
