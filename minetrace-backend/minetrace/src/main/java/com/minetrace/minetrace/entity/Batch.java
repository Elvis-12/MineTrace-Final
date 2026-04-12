package com.minetrace.minetrace.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "batches")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Batch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String batchCode;

    @Column(nullable = false)
    private String mineralType;

    @Column(nullable = false)
    private Double initialWeight;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.REGISTERED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskLevel riskLevel = RiskLevel.UNKNOWN;

    @ManyToOne
    @JoinColumn(name = "mine_id", nullable = false)
    private Mine mine;

    @Column(nullable = false)
    private LocalDateTime extractionDate;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private Double anomalyScore = 0.0;

    @Embedded
    private Flags flags = new Flags();

    @Column
    private String overrideNote;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Flags {
        private Boolean weight = false;
        private Boolean route = false;
        private Boolean duplicate = false;
        private Boolean license = false;
        private Boolean handover = false;
        private Boolean weightLoss = false;       // weight dropped between movements
        private Boolean futureExtraction = false; // extraction date is in the future
        private Boolean duplicateCode = false;    // same batch code registered more than once
    }

    public enum Status {
        REGISTERED, IN_TRANSIT, IN_STORAGE, FLAGGED, SOLD
    }

    public enum RiskLevel {
        UNKNOWN, LOW, MEDIUM, HIGH
    }
}