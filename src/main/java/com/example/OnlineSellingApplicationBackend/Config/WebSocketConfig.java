package com.example.OnlineSellingApplicationBackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register the "/ws" endpoint, enabling SockJS fallback options
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // For development, restrict in production
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Set prefix for messages FROM server TO client
        registry.enableSimpleBroker("/topic", "/queue");

        // Set prefix for messages FROM client TO server
        registry.setApplicationDestinationPrefixes("/app");

        // Enable user-specific destinations
        registry.setUserDestinationPrefix("/user");
    }
}