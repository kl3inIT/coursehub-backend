package com.coursehub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Áp dụng cho tất cả endpoint
                .allowedOrigins("http://localhost:3000", "https://coursehub.space") // Domain frontend
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Các phương thức HTTP cho phép
                .allowedHeaders("*") // Cho phép tất cả header
                .allowCredentials(true) // Cho phép gửi cookie hoặc thông tin xác thực
                .maxAge(3600); // Thời gian cache CORS preflight request (giây)
    }

}
