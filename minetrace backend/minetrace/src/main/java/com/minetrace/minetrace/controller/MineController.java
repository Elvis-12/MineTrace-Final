package com.minetrace.minetrace.controller;

import com.minetrace.minetrace.dto.MineRequest;
import com.minetrace.minetrace.dto.MineResponse;
import com.minetrace.minetrace.entity.User;
import com.minetrace.minetrace.service.AuditLogService;
import com.minetrace.minetrace.service.AuthService;
import com.minetrace.minetrace.service.MineService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mines")
@RequiredArgsConstructor
public class MineController {

    private final MineService mineService;
    private final AuthService authService;
    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<List<MineResponse>> getAll() {
        return ResponseEntity.ok(mineService.getAll());
    }

    @PostMapping
    public ResponseEntity<MineResponse> create(@Valid @RequestBody MineRequest request,
                                                @AuthenticationPrincipal UserDetails userDetails,
                                                HttpServletRequest httpRequest) {
        MineResponse response = mineService.create(request);
        User currentUser = authService.getUserByEmail(userDetails.getUsername());
        auditLogService.log("MINE_CREATED", "Mine", response.getId(), currentUser,
                httpRequest.getRemoteAddr(), "Mine registered: " + request.getName());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MineResponse> update(@PathVariable Long id,
                                                @Valid @RequestBody MineRequest request,
                                                @AuthenticationPrincipal UserDetails userDetails,
                                                HttpServletRequest httpRequest) {
        MineResponse response = mineService.update(id, request);
        User currentUser = authService.getUserByEmail(userDetails.getUsername());
        auditLogService.log("MINE_UPDATED", "Mine", String.valueOf(id), currentUser,
                httpRequest.getRemoteAddr(), "Mine updated: " + request.getName());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Boolean>> delete(@PathVariable Long id,
                                                       @AuthenticationPrincipal UserDetails userDetails,
                                                       HttpServletRequest httpRequest) {
        mineService.delete(id);
        User currentUser = authService.getUserByEmail(userDetails.getUsername());
        auditLogService.log("MINE_DELETED", "Mine", String.valueOf(id), currentUser,
                httpRequest.getRemoteAddr(), "Mine deleted");
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<Map<String, Boolean>> toggle(@PathVariable Long id,
                                                        @AuthenticationPrincipal UserDetails userDetails,
                                                        HttpServletRequest httpRequest) {
        mineService.toggleActive(id);
        User currentUser = authService.getUserByEmail(userDetails.getUsername());
        auditLogService.log("MINE_STATUS_TOGGLED", "Mine", String.valueOf(id), currentUser,
                httpRequest.getRemoteAddr(), "Mine active status toggled");
        return ResponseEntity.ok(Map.of("success", true));
    }
}
