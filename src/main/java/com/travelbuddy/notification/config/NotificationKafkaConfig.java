package com.travelbuddy.notification.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationKafkaConfig {
    @Bean
    public NewTopic notifications() {
        return new NewTopic("notifications", 6, (short)6);
    }
}