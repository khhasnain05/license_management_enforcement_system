package com.dlms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig {

    // =========================
    // CORS CONFIGURATION
    // =========================
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")

                        // IMPORTANT: avoid wildcard in production
                        .allowedOrigins(
                                "http://localhost:8080",
                                "http://localhost:3000"
                        )

                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")

                        // Only enable if you use cookies/session auth
                        .allowCredentials(true);
            }
        };
    }
}