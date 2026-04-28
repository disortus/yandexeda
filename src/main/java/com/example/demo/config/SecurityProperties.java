package com.example.demo.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {
    private List<String> corsAllowedOrigins = new ArrayList<>(List.of("http://localhost:3000", "http://localhost:5173"));
    private List<String> corsAllowedMethods = new ArrayList<>(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    private List<String> corsAllowedHeaders = new ArrayList<>(List.of("Authorization", "Content-Type", "X-Request-Id"));
    private List<String> corsExposedHeaders = new ArrayList<>(List.of("X-Request-Id"));

    public List<String> getCorsAllowedOrigins() {
        return corsAllowedOrigins;
    }

    public void setCorsAllowedOrigins(List<String> corsAllowedOrigins) {
        this.corsAllowedOrigins = corsAllowedOrigins;
    }

    public List<String> getCorsAllowedMethods() {
        return corsAllowedMethods;
    }

    public void setCorsAllowedMethods(List<String> corsAllowedMethods) {
        this.corsAllowedMethods = corsAllowedMethods;
    }

    public List<String> getCorsAllowedHeaders() {
        return corsAllowedHeaders;
    }

    public void setCorsAllowedHeaders(List<String> corsAllowedHeaders) {
        this.corsAllowedHeaders = corsAllowedHeaders;
    }

    public List<String> getCorsExposedHeaders() {
        return corsExposedHeaders;
    }

    public void setCorsExposedHeaders(List<String> corsExposedHeaders) {
        this.corsExposedHeaders = corsExposedHeaders;
    }
}