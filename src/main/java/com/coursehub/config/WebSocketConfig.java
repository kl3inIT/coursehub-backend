package com.coursehub.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.coursehub.utils.CustomHandshakeHandler;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final CustomHandshakeHandler customHandshakeHandler;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Prefix cho message gửi từ server về client
        config.enableSimpleBroker("/topic", "/queue");
        config.setUserDestinationPrefix("/user");
        // Prefix cho message gửi từ client lên server
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Cần set allowedOriginPatterns để Spring WebSocket không reject
        // Nginx sẽ override CORS headers của Spring
        registry.addEndpoint("/ws")
                .setHandshakeHandler(customHandshakeHandler)
                .setAllowedOriginPatterns("*")  // Allow all, Nginx will filter
                .withSockJS();
    }
} 