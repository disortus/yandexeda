package com.example.demo.middleware;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String requestId = UUID.randomUUID().toString();
        request.setAttribute("requestId", requestId);
        response.setHeader("X-Request-Id", requestId);

        Instant start = Instant.now();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = Duration.between(start, Instant.now()).toMillis();
            log.info("[{}] {} {} -> {} ({} ms)", requestId, request.getMethod(), request.getRequestURI(), response.getStatus(), durationMs);
        }
    }
}