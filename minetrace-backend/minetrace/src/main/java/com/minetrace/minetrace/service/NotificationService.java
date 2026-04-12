package com.minetrace.minetrace.service;

import com.minetrace.minetrace.dto.NotificationResponse;
import com.minetrace.minetrace.entity.Notification;
import com.minetrace.minetrace.entity.User;
import com.minetrace.minetrace.repository.NotificationRepository;
import com.minetrace.minetrace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
        n.setTimestamp(LocalDateTime.now());
        notificationRepository.save(n);
    }

    public void createForAllUsers(String type, String title, String message,
                                   String relatedEntityType, String relatedEntityId) {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            createForUser(user, type, title, message, relatedEntityType, relatedEntityId);
        }
    }

    public List<NotificationResponse> getNotificationsForUser(Long userId) {
        return notificationRepository.findByUserIdOrderByTimestampDesc(userId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public void markAsRead(Long notificationId, Long userId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            if (n.getUser() != null && n.getUser().getId().equals(userId)) {
                n.setRead(true);
                notificationRepository.save(n);
            }
        });
    }

    public void markAllAsRead(Long userId) {
        List<Notification> unread = notificationRepository.findByUserIdAndReadFalse(userId);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    private NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(
                String.valueOf(n.getId()),
                n.getType(),
                n.getTitle(),
                n.getMessage(),
                n.getRelatedEntityType(),
                n.getRelatedEntityId(),
                n.getRead(),
                n.getTimestamp().toString()
        );
    }
}
