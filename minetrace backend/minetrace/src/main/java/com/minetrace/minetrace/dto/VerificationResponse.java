package com.minetrace.minetrace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerificationResponse {
    private String id;
    private String batchId;
    private String checkpoint;
    private boolean passed;
    private String remarks;
    private String verifiedBy;
    private String timestamp;
}
