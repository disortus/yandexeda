package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO ресторана в ответе API")
public record RestaurantResponse(
        @Schema(description = "ID ресторана", example = "1")
        Long id,
        @Schema(description = "Название ресторана", example = "Пицца Марго")
        String name,
        @Schema(description = "Описание ресторана", example = "Итальянская кухня, пицца на тонком тесте")
        String description,
        @Schema(description = "Средний рейтинг ресторана", example = "4.75")
        BigDecimal rating,
        @Schema(description = "Дата создания ресторана (UTC)", example = "2026-04-28T20:45:10")
        LocalDateTime createdAt) {
    public static RestaurantResponse fromEntity(com.example.demo.entity.Restaurant restaurant, BigDecimal rating) {
        return new RestaurantResponse(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getDescription(),
                rating,
                restaurant.getCreatedAt()
        );
    }
}