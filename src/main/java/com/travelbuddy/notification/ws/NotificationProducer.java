package com.travelbuddy.notification.ws;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendNotification(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }
}