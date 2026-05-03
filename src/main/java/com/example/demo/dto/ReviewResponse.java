package com.example.demo.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO отзыва в ответе API")
public record ReviewResponse(
        @Schema(description = "ID отзыва", example = "1")
        Long id,
        @Schema(description = "ID пользователя", example = "1")
        Long userId,
        @Schema(description = "ID ресторана", example = "1")
        Long restaurantId,
        @Schema(description = "ID блюда", example = "1")
        Long menuItemId,
        @Schema(description = "Рейтинг (1-5)", example = "5")
        Integer rating,
        @Schema(description = "Комментарий", example = "Отличное блюдо, рекомендую!")
        String comment,
        @Schema(description = "Дата создания отзыва (UTC)", example = "2026-04-28T20:45:10")
        LocalDateTime createdAt) {
}
