package com.minetrace.minetrace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MovementRequest {
    @NotBlank
    private String batchId;

    private String batchCode;

    @NotBlank
    private String eventType;

    @NotBlank
    private String fromLocation;

    @NotBlank
    private String toLocation;

    @NotNull
    private Double weight;

    private String vehicle;
    private String driverName;
    private String notes;
    private String recordedBy;
}
