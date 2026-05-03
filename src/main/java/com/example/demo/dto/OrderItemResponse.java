package com.example.demo.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO позиции заказа в ответе API")
public record OrderItemResponse(
        @Schema(description = "ID позиции заказа", example = "1")
        Long id,
        @Schema(description = "ID заказа", example = "1")
        Long orderId,
        @Schema(description = "ID блюда", example = "1")
        Long menuItemId,
        @Schema(description = "Количество", example = "2")
        Integer quantity,
        @Schema(description = "Цена за позицию", example = "450.50")
        BigDecimal price) {
}
