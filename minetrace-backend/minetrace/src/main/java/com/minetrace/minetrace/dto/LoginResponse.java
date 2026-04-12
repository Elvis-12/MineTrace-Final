package com.minetrace.minetrace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String id;
    private String fullName;
    private String email;
    private String role;
    private String organizationName;
    private String status;
    private String createdAt;
}
