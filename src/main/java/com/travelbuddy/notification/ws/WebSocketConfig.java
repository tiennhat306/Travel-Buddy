package com.travelbuddy.notification.ws;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final CustomHandshakeHandler customHandshakeHandler;
    private final UserHandshakeHandler userHandshakeHandler;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue", "/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
//                .setAllowedOrigins("*")
                .setAllowedOrigins("http://localhost:5173", "http://localhost:8080", "https://travel-buddy-ene5.vercel.app", "https://travel-buddy-production-6a3f.up.railway.app")
                .addInterceptors(customHandshakeHandler)
                .setHandshakeHandler(userHandshakeHandler)
                .withSockJS();
    }
}