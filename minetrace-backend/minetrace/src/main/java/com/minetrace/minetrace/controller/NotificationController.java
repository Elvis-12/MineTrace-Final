package com.minetrace.minetrace.controller;

import com.minetrace.minetrace.dto.NotificationResponse;
import com.minetrace.minetrace.entity.User;
import com.minetrace.minetrace.service.AuthService;
import com.minetrace.minetrace.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAll(
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = authService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(notificationService.getNotificationsForUser(currentUser.getId()));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = authService.getUserByEmail(userDetails.getUsername());
        long count = notificationService.getUnreadCount(currentUser.getId());
        return ResponseEntity.ok(Map.of("count", count));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Map<String, Boolean>> markAsRead(@PathVariable Long id,
                                                            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = authService.getUserByEmail(userDetails.getUsername());
        notificationService.markAsRead(id, currentUser.getId());
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Map<String, Boolean>> markAllAsRead(
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = authService.getUserByEmail(userDetails.getUsername());
        notificationService.markAllAsRead(currentUser.getId());
        return ResponseEntity.ok(Map.of("success", true));
    }
}
