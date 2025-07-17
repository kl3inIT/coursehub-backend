package com.coursehub.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
    
    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

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
        if ("prod".equals(activeProfile)) {
            // Production: Chỉ cho phép frontend domain cụ thể
            registry.addEndpoint("/ws")
                    .setHandshakeHandler(customHandshakeHandler)
                    .setAllowedOrigins(
                        "https://it4beginner.vercel.app",
                        "https://coursehub.io.vn",
                        "https://www.coursehub.io.vn"
                    )
                    .withSockJS();
        } else {
            // Development: Cho phép localhost
            registry.addEndpoint("/ws")
                    .setHandshakeHandler(customHandshakeHandler)
                    .setAllowedOriginPatterns("*")
                    .withSockJS();
        }
    }
} 