package com.minetrace.minetrace.controller;

import com.minetrace.minetrace.dto.*;
import com.minetrace.minetrace.entity.User;
import com.minetrace.minetrace.service.AuditLogService;
import com.minetrace.minetrace.service.AuthService;
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
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuditLogService auditLogService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request,
                                                HttpServletRequest httpRequest) {
        LoginResponse response = authService.login(request);
        User user = authService.getUserByEmail(request.getEmail());
        auditLogService.log("USER_LOGIN", "User", response.getId(), user,
                httpRequest.getRemoteAddr(), "User logged in");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request,
                                                  @AuthenticationPrincipal UserDetails userDetails,
                                                  HttpServletRequest httpRequest) {
        UserResponse response = authService.register(request);
        User currentUser = authService.getUserByEmail(userDetails.getUsername());
        auditLogService.log("USER_CREATED", "User", response.getId(), currentUser,
                httpRequest.getRemoteAddr(), "New user registered: " + request.getEmail());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(authService.getAllUsers());
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
                                                    @Valid @RequestBody UpdateUserRequest request,
                                                    @AuthenticationPrincipal UserDetails userDetails,
                                                    HttpServletRequest httpRequest) {
        UserResponse response = authService.updateUser(id, request);
        User currentUser = authService.getUserByEmail(userDetails.getUsername());
        auditLogService.log("USER_UPDATED", "User", String.valueOf(id), currentUser,
                httpRequest.getRemoteAddr(), "User updated: " + request.getEmail());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Boolean>> activateUser(@PathVariable Long id,
                                                              @AuthenticationPrincipal UserDetails userDetails,
                                                              HttpServletRequest httpRequest) {
        authService.activateUser(id);
        User currentUser = authService.getUserByEmail(userDetails.getUsername());
        auditLogService.log("USER_ACTIVATED", "User", String.valueOf(id), currentUser,
                httpRequest.getRemoteAddr(), "User activated");
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PutMapping("/users/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Boolean>> deactivateUser(@PathVariable Long id,
                                                                @AuthenticationPrincipal UserDetails userDetails,
                                                                HttpServletRequest httpRequest) {
        authService.deactivateUser(id);
        User currentUser = authService.getUserByEmail(userDetails.getUsername());
        auditLogService.log("USER_DEACTIVATED", "User", String.valueOf(id), currentUser,
                httpRequest.getRemoteAddr(), "User deactivated");
        return ResponseEntity.ok(Map.of("success", true));
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Boolean>> deleteUser(@PathVariable Long id,
                                                            @AuthenticationPrincipal UserDetails userDetails,
                                                            HttpServletRequest httpRequest) {
        authService.deleteUser(id);
        User currentUser = authService.getUserByEmail(userDetails.getUsername());
        auditLogService.log("USER_DELETED", "User", String.valueOf(id), currentUser,
                httpRequest.getRemoteAddr(), "User permanently deleted");
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PutMapping("/profile/password")
    public ResponseEntity<Map<String, Boolean>> changePassword(
            @Valid @RequestBody PasswordChangeRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest httpRequest) {
        authService.changePassword(userDetails.getUsername(), request);
        User currentUser = authService.getUserByEmail(userDetails.getUsername());
        auditLogService.log("PASSWORD_CHANGED", "User", null, currentUser,
                httpRequest.getRemoteAddr(), "Password changed");
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/audit-logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditLogResponse>> getAuditLogs(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String action) {
        return ResponseEntity.ok(auditLogService.getAuditLogs(search, action));
    }
}
