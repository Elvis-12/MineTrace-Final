package com.minetrace.minetrace.service;

import com.minetrace.minetrace.dto.NotificationResponse;
import com.minetrace.minetrace.entity.Notification;
import com.minetrace.minetrace.entity.User;
import com.minetrace.minetrace.repository.NotificationRepository;
import com.minetrace.minetrace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public void createForUser(User user, String type, String title, String message,
                               String relatedEntityType, String relatedEntityId) {
        Notification n = new Notification();
        n.setUser(user);
        n.setType(type);
        n.setTitle(title);
        n.setMessage(message);
        n.setRelatedEntityType(relatedEntityType);
        n.setRelatedEntityId(relatedEntityId);
        n.setRead(false);
        notificationRepository.save(n);
    }

    public void createForAllAdmins(String type, String title, String message,
                                    String relatedEntityType, String relatedEntityId) {
        List<User> admins = userRepository.findAll().stream()
                .filter(u -> u.getRole() == User.Role.ADMIN && u.getStatus() == User.Status.ACTIVE)
                .collect(Collectors.toList());
        for (User admin : admins) {
            createForUser(admin, type, title, message, relatedEntityType, relatedEntityId);
        }
    }

    public List<NotificationResponse> getForUser(Long userId) {
        return notificationRepository.findByUserIdOrderByTimestampDesc(userId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    public void markRead(Long notificationId, Long userId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            if (n.getUser().getId().equals(userId)) {
                n.setRead(true);
                notificationRepository.save(n);
            }
        });
    }

    public void markAllRead(Long userId) {
        notificationRepository.findByUserIdOrderByTimestampDesc(userId).forEach(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }

    private NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(
                n.getId(),
                n.getType(),
                n.getTitle(),
                n.getMessage(),
                n.getRelatedEntityType(),
                n.getRelatedEntityId(),
                Boolean.TRUE.equals(n.getRead()),
                n.getTimestamp().toString()
        );
    }
}
