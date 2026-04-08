package com.minetrace.minetrace.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "movements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;

    @Column(nullable = false)
    private String fromLocation;

    @Column(nullable = false)
    private String toLocation;

    @Column(nullable = false)
    private Double weight;

    @Column
    private String vehicle;

    @Column
    private String driverName;

    @Column
    private String notes;

    @Column(nullable = false)
    private String recordedBy;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    public enum EventType {
        DISPATCH, RECEIVE, STORAGE, SALE, TRANSFER
    }
}
