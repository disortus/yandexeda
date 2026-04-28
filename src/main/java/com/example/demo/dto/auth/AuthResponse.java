package com.example.demo.dto.auth;

import java.time.LocalDateTime;

import com.example.demo.dto.UserResponse;
import com.example.demo.enums.TokenType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ успешной аутентификации")
public record AuthResponse(
        @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiJ9...")
        String accessToken,
        @Schema(description = "Тип токена", example = "BEARER")
        TokenType tokenType,
        @Schema(description = "Время окончания access token (UTC)", example = "2026-04-29T12:00:00")
        LocalDateTime expiresAt,
        @Schema(description = "Данные пользователя")
        UserResponse user) {
}