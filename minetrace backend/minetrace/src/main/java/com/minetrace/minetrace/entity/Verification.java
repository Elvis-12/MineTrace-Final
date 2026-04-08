package com.minetrace.minetrace.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "verifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Verification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;

    @Column(nullable = false)
    private String checkpoint;

    @Column(nullable = false)
    private Boolean passed;

    @Column
    private String remarks;

    @Column(nullable = false)
    private String verifiedBy;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();
}
