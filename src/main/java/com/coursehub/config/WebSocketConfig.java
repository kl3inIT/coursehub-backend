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
        // Spring WebSocket handle CORS - Nginx chỉ proxy
        registry.addEndpoint("/ws")
                .setHandshakeHandler(customHandshakeHandler)
                .setAllowedOrigins(
                    "http://localhost:3000",
                    "https://it4beginner.vercel.app",
                    "https://coursehub.io.vn"
                )
                .withSockJS();
    }
} 