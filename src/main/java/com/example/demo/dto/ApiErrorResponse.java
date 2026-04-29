package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.enums.ApiErrorCode;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Единый формат ошибки API. Используется и обычными контроллерами, и Spring Security handlers для 401/403.")
public record ApiErrorResponse(
        @Schema(description = "Время ошибки (UTC)", example = "2026-04-28T20:49:35")
        LocalDateTime timestamp,
        @Schema(description = "HTTP статус", example = "400")
        int status,
        @Schema(description = "Текстовый статус", example = "Bad Request")
        String error,
        @Schema(description = "Машинно-читаемый код ошибки приложения", example = "REQUEST_VALIDATION")
        ApiErrorCode code,
        @Schema(description = "Сообщение об ошибке", example = "email: Email must be valid")
        String message,
        @Schema(description = "Путь запроса", example = "/auth/register")
        String path,
        @Schema(description = "Структурированные ошибки отдельных полей. Для невалидационных ошибок возвращается пустой список.")
        List<FieldValidationError> fieldErrors) {

    public ApiErrorResponse {
        fieldErrors = fieldErrors == null ? List.of() : List.copyOf(fieldErrors);
    }
}