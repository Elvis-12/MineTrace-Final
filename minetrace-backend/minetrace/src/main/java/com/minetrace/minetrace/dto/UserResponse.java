package com.minetrace.minetrace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String fullName;
    private String email;
    private String role;
    private String organizationId;
    private String organizationName;
    private String status;
    private String createdAt;
}
