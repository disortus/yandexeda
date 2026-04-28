package com.example.demo.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Единый формат ошибки API")
public record ApiErrorResponse(
        @Schema(description = "Время ошибки (UTC)", example = "2026-04-28T20:49:35")
        LocalDateTime timestamp,
        @Schema(description = "HTTP статус", example = "400")
        int status,
        @Schema(description = "Текстовый статус", example = "Bad Request")
        String error,
        @Schema(description = "Код ошибки приложения", example = "REQUEST_VALIDATION")
        String code,
        @Schema(description = "Сообщение об ошибке", example = "email: Email must be valid")
        String message,
        @Schema(description = "Путь запроса", example = "/auth/register")
        String path) {
}