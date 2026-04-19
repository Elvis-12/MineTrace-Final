package com.minetrace.minetrace.controller;

import com.minetrace.minetrace.dto.AuditLogResponse;
import com.minetrace.minetrace.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditLogResponse>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String action) {
        return ResponseEntity.ok(auditLogService.getAuditLogs(search, action));
    }
}
