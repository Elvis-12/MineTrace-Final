package com.minetrace.minetrace.repository;

import com.minetrace.minetrace.entity.Batch;
import com.minetrace.minetrace.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT b.mine.district, b.mine.province, COUNT(b), COALESCE(SUM(b.initialWeight), 0) FROM Batch b WHERE b.mine.district IS NOT NULL GROUP BY b.mine.district, b.mine.province ORDER BY b.mine.district")
    List<Object[]> getStockByDistrictRaw();

    @Query("SELECT MIN(b.createdAt) FROM Batch b")
    java.time.LocalDateTime getOldestBatchDate();

    @Query("SELECT MAX(b.createdAt) FROM Batch b")
    java.time.LocalDateTime getNewestBatchDate();

    @Query("SELECT MIN(b.createdAt) FROM Batch b WHERE b.mine.district = :district")
    java.time.LocalDateTime getOldestBatchDateByDistrict(@Param("district") String district);

    @Query("SELECT MAX(b.createdAt) FROM Batch b WHERE b.mine.district = :district")
    java.time.LocalDateTime getNewestBatchDateByDistrict(@Param("district") String district);

    // District-filtered variants
    @Query("SELECT COUNT(b) FROM Batch b WHERE b.mine.district = :district")
    long countTotalByDistrict(@Param("district") String district);

    @Query("SELECT COALESCE(SUM(b.initialWeight), 0) FROM Batch b WHERE b.mine.district = :district")
    double sumTotalWeightByDistrict(@Param("district") String district);

    @Query("SELECT COUNT(b) FROM Batch b WHERE b.riskLevel = 'HIGH' AND b.mine.district = :district")
    long countFlaggedByDistrict(@Param("district") String district);

    @Query("SELECT b.mine.name, COUNT(b), COALESCE(SUM(b.initialWeight), 0) FROM Batch b WHERE b.mine.district = :district GROUP BY b.mine.name")
    List<Object[]> getMineProductionStatsByDistrict(@Param("district") String district);

    @Query("SELECT b.mineralType, COALESCE(SUM(b.initialWeight), 0) FROM Batch b WHERE b.mine.district = :district GROUP BY b.mineralType")
    List<Object[]> getMineralDistributionStatsByDistrict(@Param("district") String district);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Batch b SET b.inspectedBy = null WHERE b.inspectedBy = :user")
    void clearInspectedBy(User user);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Batch b SET b.createdBy = :admin WHERE b.createdBy = :user")
    void reassignCreatedBy(User user, User admin);
}
