package com.minetrace.minetrace.repository;

import com.minetrace.minetrace.entity.Verification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VerificationRepository extends JpaRepository<Verification, Long> {
    List<Verification> findByBatchIdOrderByTimestampDesc(Long batchId);
    long countByBatchId(Long batchId);
}
