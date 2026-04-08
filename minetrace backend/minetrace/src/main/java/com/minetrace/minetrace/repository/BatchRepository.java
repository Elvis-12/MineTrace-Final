package com.minetrace.minetrace.repository;

import com.minetrace.minetrace.entity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {
    Optional<Batch> findByBatchCode(String batchCode);

    List<Batch> findByMineId(Long mineId);

    List<Batch> findByRiskLevelNot(Batch.RiskLevel riskLevel);
    List<Batch> findByRiskLevel(Batch.RiskLevel riskLevel);

    @Query("SELECT COUNT(b) FROM Batch b")
    long countTotal();

    @Query("SELECT COALESCE(SUM(b.initialWeight), 0) FROM Batch b")
    double sumTotalWeight();

    @Query("SELECT COUNT(b) FROM Batch b WHERE b.riskLevel = 'HIGH'")
    long countFlagged();

    @Query("SELECT b.mine.name, COUNT(b), COALESCE(SUM(b.initialWeight), 0) FROM Batch b GROUP BY b.mine.name")
    List<Object[]> getMineProductionStats();

    @Query("SELECT b.mineralType, COALESCE(SUM(b.initialWeight), 0) FROM Batch b GROUP BY b.mineralType")
    List<Object[]> getMineralDistributionStats();
}
