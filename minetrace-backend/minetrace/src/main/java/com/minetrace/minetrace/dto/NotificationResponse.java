package com.minetrace.minetrace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private String type;
    private String title;
    private String message;
    private String relatedEntityType;
    private String relatedEntityId;
    private boolean read;
    private String timestamp;
}
