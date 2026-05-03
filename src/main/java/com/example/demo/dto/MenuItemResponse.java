package com.example.demo.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO блюда в ответе API")
public record MenuItemResponse(
        @Schema(description = "ID блюда", example = "1")
        Long id,
        @Schema(description = "ID ресторана", example = "1")
        Long restaurantId,
        @Schema(description = "ID категории", example = "1")
        Long categoryId,
        @Schema(description = "Название блюда", example = "Маргарита")
        String name,
        @Schema(description = "Описание блюда", example = "Классическая пицца с моцареллой и томатами")
        String description,
        @Schema(description = "Цена блюда", example = "450.50")
        BigDecimal price,
        @Schema(description = "Доступно ли блюдо", example = "true")
        boolean isAvailable) {
}
