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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BatchService {

    private final BatchRepository batchRepository;
    private final MineRepository mineRepository;
    private final MovementRepository movementRepository;
    private final VerificationRepository verificationRepository;

    private static final String ML_SERVICE_URL = "http://localhost:8000";
    private final RestTemplate restTemplate = new RestTemplate();

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
        List<com.minetrace.minetrace.entity.Movement> movements = movementRepository.findByBatchId(batch.getId());
        long movementCount = movements.size();
        long dispatchCount = movements.stream()
                .filter(m -> m.getEventType().name().equals("DISPATCH"))
                .count();
        long verificationCount = verificationRepository.countByBatchId(batch.getId());
        boolean hasLicense = batch.getMine().getLicenseNumber() != null
                && !batch.getMine().getLicenseNumber().isBlank();
        long daysSinceExtraction = batch.getExtractionDate() != null
                ? ChronoUnit.DAYS.between(batch.getExtractionDate().toLocalDate(), LocalDate.now())
                : 0;

        // --- Rule-based flags (always computed) ---
        Batch.Flags flags = new Batch.Flags();
        int ruleScore = 0;

        if (batch.getInitialWeight() > 5000 || batch.getInitialWeight() <= 0) {
            flags.setWeight(true);
            ruleScore++;
        }
        if (!hasLicense) {
            flags.setLicense(true);
            ruleScore++;
        }
        if (movementCount > 5) {
            flags.setRoute(true);
            ruleScore++;
        }
        if (dispatchCount > 1) {
            flags.setDuplicate(true);
            ruleScore++;
        }
        if (verificationCount == 0 && movementCount > 0) {
            flags.setHandover(true);
            ruleScore++;
        }

        // --- AI: call Isolation Forest microservice ---
        double anomalyScore = ruleScore / 5.0;          // fallback: normalised rule score
        String aiRiskLevel = null;

        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("batch_id", String.valueOf(batch.getId()));
            payload.put("initial_weight", batch.getInitialWeight());
            payload.put("movement_count", (int) movementCount);
            payload.put("dispatch_count", (int) dispatchCount);
            payload.put("verification_count", (int) verificationCount);
            payload.put("days_since_extraction", (double) daysSinceExtraction);
            payload.put("has_license", hasLicense);

            @SuppressWarnings("unchecked")
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    ML_SERVICE_URL + "/analyze", payload, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<?, ?> body = response.getBody();
                anomalyScore = ((Number) body.get("anomaly_score")).doubleValue();
                aiRiskLevel  = (String) body.get("risk_level");
            }
        } catch (Exception e) {
            // ML service unavailable — fall back to rule-based score silently
        }

        // --- Combine: AI score drives risk level when available ---
        batch.setFlags(flags);
        batch.setAnomalyScore(anomalyScore);

        Batch.RiskLevel riskLevel;
        if (aiRiskLevel != null) {
            riskLevel = Batch.RiskLevel.valueOf(aiRiskLevel);
        } else if (ruleScore == 0) {
            riskLevel = Batch.RiskLevel.LOW;
        } else if (ruleScore <= 2) {
            riskLevel = Batch.RiskLevel.MEDIUM;
        } else {
            riskLevel = Batch.RiskLevel.HIGH;
        }

        batch.setRiskLevel(riskLevel);
        if (riskLevel == Batch.RiskLevel.HIGH) {
            batch.setStatus(Batch.Status.FLAGGED);
        }
    }

    /** Called by /api/fraud/train — forwards all batches to the ML service for training. */
    public Map<String, Object> trainModel() {
        List<Batch> batches = batchRepository.findAll();
        List<Map<String, Object>> payload = batches.stream().map(b -> {
            List<com.minetrace.minetrace.entity.Movement> movements = movementRepository.findByBatchId(b.getId());
            long movementCount   = movements.size();
            long dispatchCount   = movements.stream().filter(m -> m.getEventType().name().equals("DISPATCH")).count();
            long verificationCount = verificationRepository.countByBatchId(b.getId());
            boolean hasLicense   = b.getMine().getLicenseNumber() != null && !b.getMine().getLicenseNumber().isBlank();
            long daysSince       = b.getExtractionDate() != null
                    ? ChronoUnit.DAYS.between(b.getExtractionDate().toLocalDate(), LocalDate.now()) : 0;

            Map<String, Object> item = new HashMap<>();
            item.put("batch_id",             String.valueOf(b.getId()));
            item.put("initial_weight",        b.getInitialWeight());
            item.put("movement_count",        (int) movementCount);
            item.put("dispatch_count",        (int) dispatchCount);
            item.put("verification_count",    (int) verificationCount);
            item.put("days_since_extraction", (double) daysSince);
            item.put("has_license",           hasLicense);
            return item;
        }).collect(Collectors.toList());

        try {
            Map<String, Object> body = new HashMap<>();
            body.put("batches", payload);
            @SuppressWarnings("unchecked")
            ResponseEntity<Map> resp = restTemplate.postForEntity(ML_SERVICE_URL + "/train", body, Map.class);
            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                return resp.getBody();
            }
        } catch (Exception e) {
            return Map.of("trained", false, "reason", "ML service unavailable: " + e.getMessage());
        }
        return Map.of("trained", false, "reason", "Unexpected error");
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
