package com.example.demo.middleware.example;

public record SimpleRequestLogExample(
        String method,
        String path,
        int status,
        long durationMs) {
}
