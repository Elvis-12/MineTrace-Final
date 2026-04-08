package com.minetrace.minetrace.controller;

import com.minetrace.minetrace.dto.BatchResponse;
import com.minetrace.minetrace.entity.User;
import com.minetrace.minetrace.service.AuditLogService;
import com.minetrace.minetrace.service.AuthService;
import com.minetrace.minetrace.service.BatchService;
import com.minetrace.minetrace.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fraud")
@RequiredArgsConstructor
public class FraudController {

    private final BatchService batchService;
    private final AuthService authService;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<BatchResponse>> getFraudBatches(
            @RequestParam(required = false) String riskLevel) {
        return ResponseEntity.ok(batchService.getFraudBatches(riskLevel));
    }

    @PostMapping("/analyze-all")
    public ResponseEntity<Map<String, Object>> analyzeAll(
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest httpRequest) {
        int count = batchService.analyzeAll();
        User currentUser = authService.getUserByEmail(userDetails.getUsername());
        auditLogService.log("FRAUD_ANALYSIS_ALL", "MineralBatch", null, currentUser,
                httpRequest.getRemoteAddr(), "System-wide fraud analysis run on " + count + " batches");
        notificationService.createForAllUsers("ALERT", "Fraud Analysis Complete",
                "System-wide fraud analysis completed. " + count + " batches analyzed.",
                "MineralBatch", null);
        return ResponseEntity.ok(Map.of("success", true, "analyzedCount", count));
    }
}
