package com.minetrace.minetrace.service;

import com.minetrace.minetrace.dto.AuditLogResponse;
import com.minetrace.minetrace.entity.AuditLog;
import com.minetrace.minetrace.entity.User;
import com.minetrace.minetrace.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void log(String action, String entityType, String entityId, User user, String ipAddress, String details) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setPerformedBy(user != null ? user.getFullName() : "System");
        log.setUserName(user != null ? user.getFullName() : null);
        log.setUserEmail(user != null ? user.getEmail() : null);
        log.setIpAddress(ipAddress != null ? ipAddress : "unknown");
        log.setDetails(details);
        log.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(log);
    }

    public List<AuditLogResponse> getAuditLogs(String search, String action) {
        List<AuditLog> logs = auditLogRepository.findAllByOrderByTimestampDesc();
        if (search != null && !search.isBlank()) {
            String lower = search.toLowerCase();
            logs = logs.stream()
                    .filter(a -> (a.getAction() != null && a.getAction().toLowerCase().contains(lower))
                            || (a.getPerformedBy() != null && a.getPerformedBy().toLowerCase().contains(lower))
                            || (a.getEntityType() != null && a.getEntityType().toLowerCase().contains(lower)))
                    .collect(Collectors.toList());
        }
        if (action != null && !action.isBlank()) {
            logs = logs.stream()
                    .filter(a -> action.equals(a.getAction()))
                    .collect(Collectors.toList());
        }
        return logs.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private AuditLogResponse toResponse(AuditLog log) {
        return new AuditLogResponse(
                String.valueOf(log.getId()),
                log.getAction(),
                log.getEntityType(),
                log.getEntityId(),
                log.getPerformedBy(),
                log.getUserName(),
                log.getUserEmail(),
                log.getIpAddress(),
                log.getDetails(),
                log.getTimestamp().toString()
        );
    }
}
