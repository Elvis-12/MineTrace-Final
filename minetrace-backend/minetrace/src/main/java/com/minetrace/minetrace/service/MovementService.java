package com.minetrace.minetrace.service;

import com.minetrace.minetrace.dto.MovementRequest;
import com.minetrace.minetrace.dto.MovementResponse;
import com.minetrace.minetrace.entity.Batch;
import com.minetrace.minetrace.entity.Movement;
import com.minetrace.minetrace.entity.User;
import com.minetrace.minetrace.repository.BatchRepository;
import com.minetrace.minetrace.repository.MovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovementService {

    private final MovementRepository movementRepository;
    private final BatchRepository batchRepository;

    public List<MovementResponse> getAll(String batchId) {
        List<Movement> movements;
        if (batchId != null && !batchId.isBlank()) {
            try {
                movements = movementRepository.findByBatchIdOrderByTimestampDesc(Long.parseLong(batchId));
            } catch (NumberFormatException e) {
                Optional<Batch> batch = batchRepository.findByBatchCode(batchId);
                movements = batch.map(b -> movementRepository.findByBatchIdOrderByTimestampDesc(b.getId()))
                        .orElse(List.of());
            }
        } else {
            movements = movementRepository.findAll();
        }
        return movements.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public MovementResponse create(MovementRequest request, User currentUser) {
        Batch batch;
        try {
            batch = batchRepository.findById(Long.parseLong(request.getBatchId()))
                    .orElseThrow(() -> new RuntimeException("Batch not found"));
        } catch (NumberFormatException e) {
            batch = batchRepository.findByBatchCode(request.getBatchId())
                    .orElseThrow(() -> new RuntimeException("Batch not found"));
        }

        Movement movement = new Movement();
        movement.setBatch(batch);
        movement.setEventType(Movement.EventType.valueOf(request.getEventType()));
        movement.setFromLocation(request.getFromLocation());
        movement.setToLocation(request.getToLocation());
        movement.setWeight(request.getWeight());
        movement.setVehicle(request.getVehicle());
        movement.setDriverName(request.getDriverName());
        movement.setNotes(request.getNotes());
        movement.setRecordedBy(currentUser != null ? currentUser.getFullName() : request.getRecordedBy());
        movement.setTimestamp(LocalDateTime.now());

        // Update batch status based on event type
        switch (movement.getEventType()) {
            case DISPATCH, TRANSFER -> batch.setStatus(Batch.Status.IN_TRANSIT);
            case STORAGE -> batch.setStatus(Batch.Status.IN_STORAGE);
            case SALE -> batch.setStatus(Batch.Status.SOLD);
            default -> {}
        }
        batchRepository.save(batch);

        return toResponse(movementRepository.save(movement));
    }

    public MovementResponse update(Long id, MovementRequest request) {
        Movement movement = movementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movement not found"));

        movement.setEventType(Movement.EventType.valueOf(request.getEventType()));
        movement.setFromLocation(request.getFromLocation());
        movement.setToLocation(request.getToLocation());
        movement.setWeight(request.getWeight());
        movement.setVehicle(request.getVehicle());
        movement.setDriverName(request.getDriverName());
        movement.setNotes(request.getNotes());

        return toResponse(movementRepository.save(movement));
    }

    public void delete(Long id) {
        if (!movementRepository.existsById(id)) {
            throw new RuntimeException("Movement not found");
        }
        movementRepository.deleteById(id);
    }

    private MovementResponse toResponse(Movement m) {
        return new MovementResponse(
                String.valueOf(m.getId()),
                m.getBatch() != null ? String.valueOf(m.getBatch().getId()) : null,
                m.getBatch() != null ? m.getBatch().getBatchCode() : null,
                m.getEventType().name(),
                m.getFromLocation(),
                m.getToLocation(),
                m.getWeight(),
                m.getVehicle(),
                m.getDriverName(),
                m.getNotes(),
                m.getRecordedBy(),
                m.getTimestamp().toString()
        );
    }
}
