package com.minetrace.minetrace.service;

import com.minetrace.minetrace.dto.BatchRequest;
import com.minetrace.minetrace.dto.BatchResponse;
import com.minetrace.minetrace.dto.BatchUpdateRequest;
import com.minetrace.minetrace.entity.Batch;
import com.minetrace.minetrace.entity.Mine;
import com.minetrace.minetrace.entity.User;
import com.minetrace.minetrace.repository.BatchRepository;
import com.minetrace.minetrace.repository.MineRepository;
import com.minetrace.minetrace.repository.MovementRepository;
import com.minetrace.minetrace.repository.VerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BatchService {

    private final BatchRepository batchRepository;
    private final MineRepository mineRepository;
    private final MovementRepository movementRepository;
    private final VerificationRepository verificationRepository;

    public List<BatchResponse> getAll(String search, String mineId) {
        List<Batch> batches;
        if (mineId != null && !mineId.isBlank()) {
            batches = batchRepository.findByMineId(Long.parseLong(mineId));
        } else {
            batches = batchRepository.findAll();
        }
        if (search != null && !search.isBlank()) {
            String lower = search.toLowerCase();
            batches = batches.stream()
                    .filter(b -> b.getBatchCode().toLowerCase().contains(lower)
                            || b.getMineralType().toLowerCase().contains(lower))
                    .collect(Collectors.toList());
        }
        return batches.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public BatchResponse getById(String id) {
        Optional<Batch> batch;
        try {
            batch = batchRepository.findById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            batch = batchRepository.findByBatchCode(id);
        }
        return batch.map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Batch not found: " + id));
    }

    public BatchResponse create(BatchRequest request, User createdByUser) {
        Mine mine = mineRepository.findById(Long.parseLong(request.getMineId()))
                .orElseThrow(() -> new RuntimeException("Mine not found"));

        String batchCode = generateBatchCode();

        String rawDate = request.getExtractionDate().replace("Z", "");
        LocalDateTime extractionDate;
        if (rawDate.contains("T")) {
            extractionDate = LocalDateTime.parse(rawDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } else {
            extractionDate = java.time.LocalDate.parse(rawDate, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        }

        Batch batch = new Batch();
        batch.setBatchCode(batchCode);
        batch.setMineralType(request.getMineralType());
        batch.setInitialWeight(request.getInitialWeight());
        batch.setExtractionDate(extractionDate);
        batch.setMine(mine);
        batch.setCreatedBy(createdByUser);
        batch.setStatus(Batch.Status.REGISTERED);
        batch.setRiskLevel(Batch.RiskLevel.UNKNOWN);
        batch.setAnomalyScore(0.0);
        batch.setFlags(new Batch.Flags());

        return toResponse(batchRepository.save(batch));
    }

    public BatchResponse update(Long id, BatchUpdateRequest request) {
        Batch batch = batchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Batch not found"));

        Mine mine = mineRepository.findById(Long.parseLong(request.getMineId()))
                .orElseThrow(() -> new RuntimeException("Mine not found"));

        String rawDate = request.getExtractionDate().replace("Z", "");
        LocalDateTime extractionDate;
        if (rawDate.contains("T")) {
            extractionDate = LocalDateTime.parse(rawDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } else {
            extractionDate = java.time.LocalDate.parse(rawDate, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        }

        batch.setMineralType(request.getMineralType());
        batch.setInitialWeight(request.getInitialWeight());
        batch.setExtractionDate(extractionDate);
        batch.setMine(mine);

        return toResponse(batchRepository.save(batch));
    }

    public void analyze(Long id) {
        Batch batch = batchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Batch not found"));
        performAnalysis(batch);
        batchRepository.save(batch);
    }

    public void override(Long id, String note) {
        Batch batch = batchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Batch not found"));
        batch.setOverrideNote(note);
        batchRepository.save(batch);
    }

    public List<BatchResponse> getFraudBatches(String riskLevel) {
        List<Batch> batches;
        if (riskLevel != null && !riskLevel.isBlank()) {
            batches = batchRepository.findByRiskLevel(Batch.RiskLevel.valueOf(riskLevel));
        } else {
            batches = batchRepository.findByRiskLevelNot(Batch.RiskLevel.UNKNOWN);
        }
        return batches.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        if (!batchRepository.existsById(id)) {
            throw new RuntimeException("Batch not found");
        }
        // Remove child records first
        verificationRepository.findByBatchIdOrderByTimestampDesc(id)
                .forEach(v -> verificationRepository.deleteById(v.getId()));
        movementRepository.findByBatchId(id)
                .forEach(m -> movementRepository.deleteById(m.getId()));
        batchRepository.deleteById(id);
    }

    public int analyzeAll() {
        List<Batch> batches = batchRepository.findAll();
        for (Batch batch : batches) {
            performAnalysis(batch);
        }
        batchRepository.saveAll(batches);
        return batches.size();
    }

    private void performAnalysis(Batch batch) {
        Batch.Flags flags = new Batch.Flags();
        int score = 0;

        // Weight flag: very high or zero weight
        if (batch.getInitialWeight() > 5000 || batch.getInitialWeight() <= 0) {
            flags.setWeight(true);
            score++;
        }

        // License flag: mine has no license number
        if (batch.getMine().getLicenseNumber() == null || batch.getMine().getLicenseNumber().isBlank()) {
            flags.setLicense(true);
            score++;
        }

        // Route flag: check if movements exist
        long movementCount = movementRepository.findByBatchId(batch.getId()).size();
        if (movementCount > 5) {
            flags.setRoute(true);
            score++;
        }

        // Duplicate flag: same batch code dispatched more than once
        long dispatchCount = movementRepository.findByBatchId(batch.getId()).stream()
                .filter(m -> m.getEventType().name().equals("DISPATCH"))
                .count();
        if (dispatchCount > 1) {
            flags.setDuplicate(true);
            score++;
        }

        // Handover flag: no verifications for this batch
        long verificationCount = verificationRepository.countByBatchId(batch.getId());
        if (verificationCount == 0 && movementCount > 0) {
            flags.setHandover(true);
            score++;
        }

        batch.setFlags(flags);
        batch.setAnomalyScore((double) score);

        if (score == 0) {
            batch.setRiskLevel(Batch.RiskLevel.LOW);
        } else if (score <= 2) {
            batch.setRiskLevel(Batch.RiskLevel.MEDIUM);
        } else {
            batch.setRiskLevel(Batch.RiskLevel.HIGH);
        }

        if (score >= 3) {
            batch.setStatus(Batch.Status.FLAGGED);
        }
    }

    private String generateBatchCode() {
        int year = LocalDateTime.now().getYear();
        long count = batchRepository.count() + 1;
        return String.format("MT-%d-%03d", year, count);
    }

    public BatchResponse toResponse(Batch batch) {
        BatchResponse.FlagsDto flagsDto = new BatchResponse.FlagsDto(
                batch.getFlags() != null && Boolean.TRUE.equals(batch.getFlags().getWeight()),
                batch.getFlags() != null && Boolean.TRUE.equals(batch.getFlags().getRoute()),
                batch.getFlags() != null && Boolean.TRUE.equals(batch.getFlags().getDuplicate()),
                batch.getFlags() != null && Boolean.TRUE.equals(batch.getFlags().getLicense()),
                batch.getFlags() != null && Boolean.TRUE.equals(batch.getFlags().getHandover())
        );

        return new BatchResponse(
                String.valueOf(batch.getId()),
                batch.getBatchCode(),
                batch.getMineralType(),
                batch.getInitialWeight(),
                batch.getStatus().name(),
                batch.getRiskLevel().name(),
                batch.getMine() != null ? String.valueOf(batch.getMine().getId()) : null,
                batch.getMine() != null ? batch.getMine().getName() : "",
                batch.getExtractionDate().toString(),
                batch.getCreatedBy() != null ? batch.getCreatedBy().getFullName() : "",
                batch.getCreatedAt().toString(),
                batch.getAnomalyScore() != null ? batch.getAnomalyScore() : 0.0,
                flagsDto,
                batch.getOverrideNote()
        );
    }
}
