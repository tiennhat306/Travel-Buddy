package com.travelbuddy.notification.ws;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageService {
    private final SimpMessagingTemplate messagingTemplate;

    private static final String queue = "/queue/notifications";

    public void sendNotification(String userId, String message) {
        messagingTemplate.convertAndSendToUser(userId, queue, message);
    }
}
