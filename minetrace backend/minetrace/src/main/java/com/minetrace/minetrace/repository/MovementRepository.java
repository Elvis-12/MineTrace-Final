package com.minetrace.minetrace.repository;

import com.minetrace.minetrace.entity.Movement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovementRepository extends JpaRepository<Movement, Long> {
    List<Movement> findByBatchId(Long batchId);
    List<Movement> findByBatchIdOrderByTimestampDesc(Long batchId);
}
