package com.example.demo.security.example;

public class SimpleTokenCheckerExample {
    private static final String BEARER_PREFIX = "Bearer ";

    public boolean isBearerToken(String authorizationHeader) {
        return authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX);
    }

    public String extractRawToken(String authorizationHeader) {
        if (!isBearerToken(authorizationHeader)) {
            return "";
        }

        return authorizationHeader.substring(BEARER_PREFIX.length()).trim();
    }
}
