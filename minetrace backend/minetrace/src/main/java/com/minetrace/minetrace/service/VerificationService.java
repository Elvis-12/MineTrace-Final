package com.minetrace.minetrace.service;

import com.minetrace.minetrace.dto.VerificationRequest;
import com.minetrace.minetrace.dto.VerificationResponse;
import com.minetrace.minetrace.entity.Batch;
import com.minetrace.minetrace.entity.User;
import com.minetrace.minetrace.entity.Verification;
import com.minetrace.minetrace.repository.BatchRepository;
import com.minetrace.minetrace.repository.VerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final VerificationRepository verificationRepository;
    private final BatchRepository batchRepository;

    public VerificationResponse create(VerificationRequest request, User currentUser) {
        Batch batch;
        try {
            batch = batchRepository.findById(Long.parseLong(request.getBatchId()))
                    .orElseThrow(() -> new RuntimeException("Batch not found"));
        } catch (NumberFormatException e) {
            batch = batchRepository.findByBatchCode(request.getBatchId())
                    .orElseThrow(() -> new RuntimeException("Batch not found"));
        }

        Verification v = new Verification();
        v.setBatch(batch);
        v.setCheckpoint(request.getCheckpoint());
        v.setPassed(request.getPassed());
        v.setRemarks(request.getRemarks());
        v.setVerifiedBy(currentUser != null ? currentUser.getFullName() : request.getVerifiedBy());
        v.setTimestamp(LocalDateTime.now());

        return toResponse(verificationRepository.save(v));
    }

    public List<VerificationResponse> getByBatchId(String batchId) {
        Long id;
        try {
            id = Long.parseLong(batchId);
        } catch (NumberFormatException e) {
            Optional<Batch> batch = batchRepository.findByBatchCode(batchId);
            id = batch.map(Batch::getId).orElseThrow(() -> new RuntimeException("Batch not found"));
        }
        return verificationRepository.findByBatchIdOrderByTimestampDesc(id)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private VerificationResponse toResponse(Verification v) {
        return new VerificationResponse(
                String.valueOf(v.getId()),
                v.getBatch() != null ? String.valueOf(v.getBatch().getId()) : null,
                v.getCheckpoint(),
                v.getPassed(),
                v.getRemarks(),
                v.getVerifiedBy(),
                v.getTimestamp().toString()
        );
    }
}
