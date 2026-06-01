package com.minetrace.minetrace.controller;

import com.minetrace.minetrace.dto.*;
import com.minetrace.minetrace.entity.User;
import com.minetrace.minetrace.service.AuditLogService;
import com.minetrace.minetrace.service.AuthService;
import com.minetrace.minetrace.service.BatchService;
import com.minetrace.minetrace.service.QrCodeService;
import com.minetrace.minetrace.service.VerificationService;
import com.minetrace.minetrace.service.WebSocketEventService;
import com.minetrace.minetrace.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
    private final QrCodeService qrCodeService;
    private final WebSocketEventService webSocketEventService;
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
        webSocketEventService.broadcastFraudAnalyzed(String.valueOf(id));
        BatchResponse analyzed = batchService.getById(String.valueOf(id));
        if ("HIGH".equals(analyzed.getRiskLevel())) {
            notificationService.createForAllAdmins(
                    "HIGH_RISK", "High Risk Batch Detected",
                    "Batch " + analyzed.getBatchCode() + " has been flagged as HIGH RISK. Immediate review required.",
                    "MineralBatch", String.valueOf(id));
        }
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

    @PostMapping("/{id}/inspect")
    @PreAuthorize("hasAnyRole('INSPECTOR', 'ADMIN')")
    public ResponseEntity<Map<String, Boolean>> inspect(@PathVariable Long id,
                                                         @RequestBody Map<String, Object> body,
                                                         @AuthenticationPrincipal UserDetails userDetails,
                                                         HttpServletRequest httpRequest) {
        boolean approved = Boolean.TRUE.equals(body.get("approved"));
        String note = (String) body.getOrDefault("note", "");
        User currentUser = authService.getUserByEmail(userDetails.getUsername());
        batchService.inspect(id, approved, note, currentUser);
        auditLogService.log(approved ? "BATCH_APPROVED" : "BATCH_FLAGGED_INSPECTION",
                "MineralBatch", String.valueOf(id), currentUser,
                httpRequest.getRemoteAddr(), "Inspector " + (approved ? "approved" : "flagged") + " batch: " + note);
        webSocketEventService.broadcastBatchUpdated(String.valueOf(id));
        BatchResponse inspected = batchService.getById(String.valueOf(id));
        String notifTitle = approved ? "Batch Approved by Inspector" : "Batch Flagged by Inspector";
        String notifMsg = approved
                ? "Batch " + inspected.getBatchCode() + " has been approved by inspector."
                : "Batch " + inspected.getBatchCode() + " has been flagged by inspector: " + note;
        notificationService.createForAllAdmins(
                approved ? "BATCH_APPROVED" : "BATCH_FLAGGED",
                notifTitle, notifMsg, "MineralBatch", String.valueOf(id));
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/pending-inspection")
    @PreAuthorize("hasAnyRole('INSPECTOR', 'ADMIN')")
    public ResponseEntity<List<BatchResponse>> getPendingInspection() {
        return ResponseEntity.ok(batchService.getPendingInspection());
    }

    @GetMapping("/{batchId}/verifications")
    public ResponseEntity<List<VerificationResponse>> getVerifications(@PathVariable String batchId) {
        return ResponseEntity.ok(verificationService.getByBatchId(batchId));
    }

    @GetMapping("/{id}/qr")
    public ResponseEntity<byte[]> downloadQr(@PathVariable String id) {
        BatchResponse batch = batchService.getById(id);
        String verificationUrl = System.getenv("FRONTEND_URL") != null
                ? System.getenv("FRONTEND_URL") : "http://localhost:5173";
        verificationUrl += "/verification?scan=" + batch.getBatchCode();
        byte[] png = qrCodeService.generatePngBytes(verificationUrl);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + batch.getBatchCode() + "-QR.png\"")
                .body(png);
    }
}
