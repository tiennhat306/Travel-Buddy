package com.travelbuddy.notification.ws;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @MessageMapping("/sendNotification")
    @SendToUser("/queue/notifications")
    public void sendNotification(String message, Principal principal) {
        messageService.sendNotification(principal.getName(), message);
    }
}
