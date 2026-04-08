package com.minetrace.minetrace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BatchRequest {
    @NotBlank
    private String mineralType;

    @NotNull
    private Double initialWeight;

    @NotBlank
    private String extractionDate;

    @NotBlank
    private String mineId;

    private String mineName;
    private String createdBy;
    private String notes;
}
