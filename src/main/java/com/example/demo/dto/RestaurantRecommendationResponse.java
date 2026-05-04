package com.example.demo.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Рекомендованный ресторан для пользователя")
public record RestaurantRecommendationResponse(
        @Schema(description = "ID ресторана", example = "12")
        Long restaurantId,
        @Schema(description = "Название ресторана", example = "Sushi Point")
        String restaurantName,
        @Schema(description = "Рейтинг ресторана", example = "4.70")
        BigDecimal rating,
        @Schema(description = "Оценка релевантности рекомендации", example = "8")
        Long relevanceScore) {
}