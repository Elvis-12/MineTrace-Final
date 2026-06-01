package com.minetrace.minetrace.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class WebSocketEventService {

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastMovement(String batchId) {
        messagingTemplate.convertAndSend("/topic/updates",
                Map.of("type", "MOVEMENT_RECORDED", "batchId", batchId));
    }

    public void broadcastBatchUpdated(String batchId) {
        messagingTemplate.convertAndSend("/topic/updates",
                Map.of("type", "BATCH_UPDATED", "batchId", batchId));
    }

    public void broadcastFraudAnalyzed(String batchId) {
        messagingTemplate.convertAndSend("/topic/updates",
                Map.of("type", "FRAUD_ANALYZED", "batchId", batchId));
    }
}
