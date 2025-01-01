package com.travelbuddy.notification;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic notifications() {
        return new NewTopic("notifications", 6, (short)6);
    }
}