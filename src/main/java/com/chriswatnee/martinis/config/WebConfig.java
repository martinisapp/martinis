package com.chriswatnee.martinis.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Static resources are now automatically served from src/main/resources/static/
    // by Spring Boot's default configuration

    // Multipart configuration is handled via application.properties:
    // spring.servlet.multipart.max-file-size=50MB
    // spring.servlet.multipart.max-request-size=50MB
}
