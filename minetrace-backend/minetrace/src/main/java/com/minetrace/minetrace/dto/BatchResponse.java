package com.minetrace.minetrace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchResponse {
    private String id;
    private String batchCode;
    private String mineralType;
    private double initialWeight;
    private String status;
    private String riskLevel;
    private String mineId;
    private String mineName;
    private String extractionDate;
    private String createdBy;
    private String createdAt;
    private double anomalyScore;
    private FlagsDto flags;
    private String overrideNote;

    // Inspector compliance fields
    private Boolean inspectorApproved;
    private String inspectorNote;
    private String inspectedBy;
    private String inspectedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlagsDto {
        private boolean weight;
        private boolean route;
        private boolean duplicate;
        private boolean license;
        private boolean handover;
        private boolean weightLoss;
        private boolean futureExtraction;
    }
}
