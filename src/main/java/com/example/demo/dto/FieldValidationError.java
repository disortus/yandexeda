package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Структурированная ошибка валидации одного поля входного DTO")
public record FieldValidationError(
        @Schema(description = "Название поля из JSON/request DTO", example = "email")
        String field,
        @Schema(description = "Сообщение, объясняющее почему значение поля не прошло проверку", example = "Email must be valid")
        String message) {
}