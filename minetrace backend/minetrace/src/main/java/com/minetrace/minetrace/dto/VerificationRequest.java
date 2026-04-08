package com.minetrace.minetrace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VerificationRequest {
    @NotBlank
    private String batchId;

    @NotBlank
    private String checkpoint;

    @NotNull
    private Boolean passed;

    private String remarks;
    private String verifiedBy;
}
