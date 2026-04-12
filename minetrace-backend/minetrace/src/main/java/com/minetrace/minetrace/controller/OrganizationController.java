package com.minetrace.minetrace.controller;

import com.minetrace.minetrace.dto.OrganizationRequest;
import com.minetrace.minetrace.dto.OrganizationResponse;
import com.minetrace.minetrace.entity.User;
import org.springframework.security.access.prepost.PreAuthorize;
import com.minetrace.minetrace.service.AuditLogService;
import com.minetrace.minetrace.service.AuthService;
import com.minetrace.minetrace.service.OrganizationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;
    private final AuthService authService;
    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<List<OrganizationResponse>> getAll() {
        return ResponseEntity.ok(organizationService.getAll());
    }

    @PostMapping
    public ResponseEntity<OrganizationResponse> create(@Valid @RequestBody OrganizationRequest request,
                                                        @AuthenticationPrincipal UserDetails userDetails,
                                                        HttpServletRequest httpRequest) {
        OrganizationResponse response = organizationService.create(request);
        User currentUser = authService.getUserByEmail(userDetails.getUsername());
        auditLogService.log("ORGANIZATION_CREATED", "Organization", response.getId(), currentUser,
                httpRequest.getRemoteAddr(), "Organization created: " + request.getName());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Boolean>> delete(@PathVariable Long id,
                                                       @AuthenticationPrincipal UserDetails userDetails,
                                                       HttpServletRequest httpRequest) {
        organizationService.delete(id);
        User currentUser = authService.getUserByEmail(userDetails.getUsername());
        auditLogService.log("ORGANIZATION_DELETED", "Organization", String.valueOf(id), currentUser,
                httpRequest.getRemoteAddr(), "Organization deleted");
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrganizationResponse> update(@PathVariable Long id,
                                                        @Valid @RequestBody OrganizationRequest request,
                                                        @AuthenticationPrincipal UserDetails userDetails,
                                                        HttpServletRequest httpRequest) {
        OrganizationResponse response = organizationService.update(id, request);
        User currentUser = authService.getUserByEmail(userDetails.getUsername());
        auditLogService.log("ORGANIZATION_UPDATED", "Organization", String.valueOf(id), currentUser,
                httpRequest.getRemoteAddr(), "Organization updated: " + request.getName());
        return ResponseEntity.ok(response);
    }
}
