package com.minetrace.minetrace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {
    private String id;
    private String action;
    private String entityType;
    private String entityId;
    private String performedBy;
    private String userName;
    private String userEmail;
    private String ipAddress;
    private String details;
    private String timestamp;
}
