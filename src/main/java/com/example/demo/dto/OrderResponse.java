package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO заказа в ответе API")
public record OrderResponse(
        @Schema(description = "ID заказа", example = "1")
        Long id,
        @Schema(description = "ID покупателя", example = "1")
        Long customerId,
        @Schema(description = "ID курьера", example = "2")
        Long courierId,
        @Schema(description = "ID ресторана", example = "1")
        Long restaurantId,
        @Schema(description = "ID адреса доставки", example = "1")
        Long deliveryAddressId,
        @Schema(description = "Статус заказа", example = "pending")
        String status,
        @Schema(description = "Итоговая стоимость заказа", example = "850.75")
        BigDecimal totalPrice,
        @Schema(description = "Дата создания заказа (UTC)", example = "2026-04-28T20:45:10")
        LocalDateTime createdAt) {
}
