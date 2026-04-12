package com.minetrace.minetrace.service;

import com.minetrace.minetrace.dto.BatchResponse;
import com.minetrace.minetrace.dto.MovementResponse;
import com.minetrace.minetrace.entity.Batch;
import com.minetrace.minetrace.entity.Movement;
import com.minetrace.minetrace.repository.BatchRepository;
import com.minetrace.minetrace.repository.MineRepository;
import com.minetrace.minetrace.repository.MovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final BatchRepository batchRepository;
    private final MovementRepository movementRepository;
    private final MineRepository mineRepository;
    private final BatchService batchService;
    private final MovementService movementService;

    public Map<String, Object> getSummary() {
        long totalBatches = batchRepository.countTotal();
        double totalWeight = batchRepository.sumTotalWeight();
        long activeMines = mineRepository.findByActiveTrue().size();
        long flaggedBatches = batchRepository.countFlagged();

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalBatches", totalBatches);
        summary.put("totalWeight", totalWeight);
        summary.put("activeMines", activeMines);
        summary.put("flaggedBatches", flaggedBatches);
        return summary;
    }

    public Map<String, Object> getMineProduction() {
        List<Object[]> stats = batchRepository.getMineProductionStats();
        List<Map<String, Object>> data = new ArrayList<>();
        for (Object[] row : stats) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("mineName", row[0]);
            item.put("totalBatches", ((Number) row[1]).longValue());
            item.put("totalWeight", ((Number) row[2]).doubleValue());
            data.add(item);
        }
        return Map.of("data", data);
    }

    public Map<String, Object> getMineralDistribution() {
        List<Object[]> stats = batchRepository.getMineralDistributionStats();
        double grandTotal = stats.stream()
                .mapToDouble(r -> ((Number) r[1]).doubleValue())
                .sum();

        List<Map<String, Object>> data = new ArrayList<>();
        for (Object[] row : stats) {
            double weight = ((Number) row[1]).doubleValue();
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("mineralType", row[0]);
            item.put("totalWeight", weight);
            item.put("percentage", grandTotal > 0 ? Math.round((weight / grandTotal) * 10000.0) / 100.0 : 0);
            data.add(item);
        }
        return Map.of("data", data);
    }

    public Map<String, Object> getProductionReport(String startDate, String endDate) {
        List<Batch> batches = filterBatchesByDate(batchRepository.findAll(), startDate, endDate);
        return Map.of("batches", batches.stream().map(batchService::toResponse).collect(Collectors.toList()));
    }

    public Map<String, Object> getMovementReport(String startDate, String endDate) {
        List<Movement> movements = movementRepository.findAll();
        if (startDate != null && !startDate.isBlank()) {
            LocalDateTime start = parseDate(startDate);
            movements = movements.stream().filter(m -> !m.getTimestamp().isBefore(start)).collect(Collectors.toList());
        }
        if (endDate != null && !endDate.isBlank()) {
            LocalDateTime end = parseDate(endDate);
            movements = movements.stream().filter(m -> !m.getTimestamp().isAfter(end)).collect(Collectors.toList());
        }
        List<MovementResponse> responses = movements.stream()
                .map(m -> new MovementResponse(
                        String.valueOf(m.getId()),
                        m.getBatch() != null ? String.valueOf(m.getBatch().getId()) : null,
                        m.getBatch() != null ? m.getBatch().getBatchCode() : null,
                        m.getEventType().name(), m.getFromLocation(), m.getToLocation(),
                        m.getWeight(), m.getVehicle(), m.getDriverName(), m.getNotes(),
                        m.getRecordedBy(), m.getTimestamp().toString()))
                .collect(Collectors.toList());
        return Map.of("movements", responses);
    }

    public Map<String, Object> getComplianceReport(String startDate, String endDate) {
        List<Batch> batches = filterBatchesByDate(batchRepository.findAll(), startDate, endDate);
        List<BatchResponse> unverified = batches.stream()
                .filter(b -> b.getStatus() == Batch.Status.REGISTERED)
                .map(batchService::toResponse)
                .collect(Collectors.toList());
        return Map.of("unverifiedBatches", unverified);
    }

    public Map<String, Object> getRiskReport(String startDate, String endDate) {
        List<Batch> batches = filterBatchesByDate(batchRepository.findAll(), startDate, endDate);
        List<BatchResponse> highRisk = batches.stream()
                .filter(b -> b.getRiskLevel() == Batch.RiskLevel.HIGH)
                .map(batchService::toResponse)
                .collect(Collectors.toList());
        return Map.of("highRiskBatches", highRisk);
    }

    private List<Batch> filterBatchesByDate(List<Batch> batches, String startDate, String endDate) {
        if (startDate != null && !startDate.isBlank()) {
            LocalDateTime start = parseDate(startDate);
            batches = batches.stream().filter(b -> !b.getCreatedAt().isBefore(start)).collect(Collectors.toList());
        }
        if (endDate != null && !endDate.isBlank()) {
            LocalDateTime end = parseDate(endDate);
            batches = batches.stream().filter(b -> !b.getCreatedAt().isAfter(end)).collect(Collectors.toList());
        }
        return batches;
    }

    private LocalDateTime parseDate(String dateStr) {
        try {
            return LocalDateTime.parse(dateStr.replace("Z", ""), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            return LocalDateTime.parse(dateStr + "T00:00:00");
        }
    }
}
