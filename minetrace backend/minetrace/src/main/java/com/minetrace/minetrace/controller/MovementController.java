package com.minetrace.minetrace.controller;

import com.minetrace.minetrace.dto.MovementRequest;
import com.minetrace.minetrace.dto.MovementResponse;
import com.minetrace.minetrace.entity.User;
import com.minetrace.minetrace.service.AuditLogService;
import com.minetrace.minetrace.service.AuthService;
import com.minetrace.minetrace.service.MovementService;
import com.minetrace.minetrace.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/movements")
@RequiredArgsConstructor
public class MovementController {

    private final MovementService movementService;
    private final AuthService authService;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<MovementResponse>> getAll(
            @RequestParam(required = false) String batchId) {
        return ResponseEntity.ok(movementService.getAll(batchId));
    }

    @PostMapping
    public ResponseEntity<MovementResponse> create(@Valid @RequestBody MovementRequest request,
                                                    @AuthenticationPrincipal UserDetails userDetails,
                                                    HttpServletRequest httpRequest) {
        User currentUser = authService.getUserByEmail(userDetails.getUsername());
        MovementResponse response = movementService.create(request, currentUser);
        auditLogService.log("MOVEMENT_RECORDED", "Movement", response.getId(), currentUser,
                httpRequest.getRemoteAddr(),
                "Movement " + response.getEventType() + " recorded for batch " + response.getBatchCode());
        if ("DISPATCH".equals(response.getEventType())) {
            notificationService.createForAllUsers("BATCH_DISPATCHED", "Batch Dispatched",
                    "Batch " + response.getBatchCode() + " dispatched from " + response.getFromLocation()
                            + " to " + response.getToLocation(),
                    "Movement", response.getId());
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovementResponse> update(@PathVariable Long id,
                                                    @Valid @RequestBody MovementRequest request,
                                                    @AuthenticationPrincipal UserDetails userDetails,
                                                    HttpServletRequest httpRequest) {
        MovementResponse response = movementService.update(id, request);
        User currentUser = authService.getUserByEmail(userDetails.getUsername());
        auditLogService.log("MOVEMENT_UPDATED", "Movement", String.valueOf(id), currentUser,
                httpRequest.getRemoteAddr(), "Movement updated for batch " + response.getBatchCode());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Boolean>> delete(@PathVariable Long id,
                                                        @AuthenticationPrincipal UserDetails userDetails,
                                                        HttpServletRequest httpRequest) {
        movementService.delete(id);
        User currentUser = authService.getUserByEmail(userDetails.getUsername());
        auditLogService.log("MOVEMENT_DELETED", "Movement", String.valueOf(id), currentUser,
                httpRequest.getRemoteAddr(), "Movement deleted");
        return ResponseEntity.ok(Map.of("success", true));
    }
}
