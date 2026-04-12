package com.minetrace.minetrace.controller;

import com.minetrace.minetrace.dto.VerificationRequest;
import com.minetrace.minetrace.dto.VerificationResponse;
import com.minetrace.minetrace.entity.User;
import com.minetrace.minetrace.service.AuditLogService;
import com.minetrace.minetrace.service.AuthService;
import com.minetrace.minetrace.service.NotificationService;
import com.minetrace.minetrace.service.VerificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/verifications")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationService verificationService;
    private final AuthService authService;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<VerificationResponse> create(@Valid @RequestBody VerificationRequest request,
                                                        @AuthenticationPrincipal UserDetails userDetails,
                                                        HttpServletRequest httpRequest) {
        User currentUser = authService.getUserByEmail(userDetails.getUsername());
        VerificationResponse response = verificationService.create(request, currentUser);
        auditLogService.log("VERIFICATION_RECORDED", "Verification", response.getId(), currentUser,
                httpRequest.getRemoteAddr(),
                "Verification at " + request.getCheckpoint() + " - passed: " + request.getPassed());
        if (!request.getPassed()) {
            notificationService.createForAllUsers("VERIFICATION_FAILED", "Verification Failed",
                    "Batch verification failed at checkpoint: " + request.getCheckpoint(),
                    "Verification", response.getId());
        }
        return ResponseEntity.ok(response);
    }
}
