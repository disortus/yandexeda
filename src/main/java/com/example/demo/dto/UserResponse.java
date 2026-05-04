package com.example.demo.dto;

import java.time.LocalDateTime;

import com.example.demo.enums.UserRole;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO пользователя в ответе API")
public record UserResponse(
        @Schema(description = "ID пользователя", example = "42")
        Long id,
        @Schema(description = "Имя пользователя", example = "alice")
        String username,
        @Schema(description = "Телефон пользователя", example = "7012345678")
        String phone,
        @Schema(description = "Роль пользователя", example = "CUSTOMER")
        UserRole role,
        @Schema(description = "Дата создания пользователя (UTC)", example = "2026-04-28T20:45:10")
        LocalDateTime createdAt) {
}