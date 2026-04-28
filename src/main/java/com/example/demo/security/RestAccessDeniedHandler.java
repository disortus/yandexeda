package com.example.demo.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.example.demo.enums.ApiErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(toJson(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                ApiErrorCode.ACCESS_DENIED.name(),
                "You do not have enough permissions for this action",
                request.getRequestURI()));
    }

    private String toJson(LocalDateTime timestamp, int status, String error, String code, String message, String path)
            throws JsonProcessingException {
        return "{\"timestamp\":\"" + timestamp + "\","
                + "\"status\":" + status + ","
                + "\"error\":\"" + escape(error) + "\","
                + "\"code\":\"" + escape(code) + "\","
                + "\"message\":\"" + escape(message) + "\","
                + "\"path\":\"" + escape(path) + "\"}";
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}