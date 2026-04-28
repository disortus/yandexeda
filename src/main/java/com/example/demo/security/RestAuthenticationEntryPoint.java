package com.example.demo.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.example.demo.enums.ApiErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(toJson(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                ApiErrorCode.AUTHENTICATION_FAILED.name(),
                "Authentication required or token is invalid",
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