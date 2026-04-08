package com.minetrace.minetrace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovementResponse {
    private String id;
    private String batchId;
    private String batchCode;
    private String eventType;
    private String fromLocation;
    private String toLocation;
    private double weight;
    private String vehicle;
    private String driverName;
    private String notes;
    private String recordedBy;
    private String timestamp;
}
