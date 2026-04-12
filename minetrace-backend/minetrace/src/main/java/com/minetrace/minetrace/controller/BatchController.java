package com.minetrace.minetrace.controller;

import com.minetrace.minetrace.dto.*;
import com.minetrace.minetrace.entity.User;
import com.minetrace.minetrace.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/batches")
@RequiredArgsConstructor
public class BatchController {

    private final BatchService batchService;
    private final VerificationService verificationService;
    private final AuthService authService;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<BatchResponse>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String mineId) {
        return ResponseEntity.ok(batchService.getAll(search, mineId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BatchResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(batchService.getById(id));
    }

    @PostMapping
    public ResponseEntity<BatchResponse> create(@Valid @RequestBody BatchRequest request,
                                                 @AuthenticationPrincipal UserDetails userDetails,
                                                 HttpServletRequest httpRequest) {
        User currentUser = authService.getUserByEmail(userDetails.getUsername());
        BatchResponse response = batchService.create(request, currentUser);
        auditLogService.log("BATCH_CREATED", "MineralBatch", response.getId(), currentUser,
                httpRequest.getRemoteAddr(), "Batch created: " + response.getBatchCode());
        notificationService.createForAllUsers("SUCCESS", "New Batch Registered",
                "Batch " + response.getBatchCode() + " (" + response.getMineralType() + ") has been registered.",
                "MineralBatch", response.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BatchResponse> update(@PathVariable Long id,
                                                 @Valid @RequestBody BatchUpdateRequest request,
                                                 @AuthenticationPrincipal UserDetails userDetails,
                                                 HttpServletRequest httpRequest) {
        BatchResponse response = batchService.update(id, request);
        User currentUser = authService.getUserByEmail(userDetails.getUsername());
        auditLogService.log("BATCH_UPDATED", "MineralBatch", String.valueOf(id), currentUser,
                httpRequest.getRemoteAddr(), "Batch updated: " + response.getBatchCode());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Boolean>> delete(@PathVariable Long id,
                                                       @AuthenticationPrincipal UserDetails userDetails,
                                                       HttpServletRequest httpRequest) {
        batchService.delete(id);
        User currentUser = authService.getUserByEmail(userDetails.getUsername());
        auditLogService.log("BATCH_DELETED", "MineralBatch", String.valueOf(id), currentUser,
                httpRequest.getRemoteAddr(), "Batch deleted");
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/{id}/analyze")
    public ResponseEntity<Map<String, Boolean>> analyze(@PathVariable Long id,
                                                         @AuthenticationPrincipal UserDetails userDetails,
                                                         HttpServletRequest httpRequest) {
        batchService.analyze(id);
        User currentUser = authService.getUserByEmail(userDetails.getUsername());
        auditLogService.log("BATCH_ANALYZED", "MineralBatch", String.valueOf(id), currentUser,
                httpRequest.getRemoteAddr(), "Fraud analysis run on batch " + id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PutMapping("/{id}/override")
    public ResponseEntity<Map<String, Boolean>> override(@PathVariable Long id,
                                                          @Valid @RequestBody OverrideRequest request,
                                                          @AuthenticationPrincipal UserDetails userDetails,
                                                          HttpServletRequest httpRequest) {
        batchService.override(id, request.getNote());
        User currentUser = authService.getUserByEmail(userDetails.getUsername());
        auditLogService.log("RISK_OVERRIDE", "MineralBatch", String.valueOf(id), currentUser,
                httpRequest.getRemoteAddr(), "Risk overridden: " + request.getNote());
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/{batchId}/verifications")
    public ResponseEntity<List<VerificationResponse>> getVerifications(@PathVariable String batchId) {
        return ResponseEntity.ok(verificationService.getByBatchId(batchId));
    }
}
