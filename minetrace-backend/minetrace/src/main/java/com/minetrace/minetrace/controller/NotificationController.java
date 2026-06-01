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
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = authService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(notificationService.getForUser(user.getId()));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = authService.getUserByEmail(userDetails.getUsername());
        long count = notificationService.getUnreadCount(user.getId());
        return ResponseEntity.ok(Map.of("count", count));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Map<String, Boolean>> markRead(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = authService.getUserByEmail(userDetails.getUsername());
        notificationService.markRead(id, user.getId());
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PutMapping("/read-all")
    public ResponseEntity<Map<String, Boolean>> markAllRead(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = authService.getUserByEmail(userDetails.getUsername());
        notificationService.markAllRead(user.getId());
        return ResponseEntity.ok(Map.of("success", true));
    }
}
