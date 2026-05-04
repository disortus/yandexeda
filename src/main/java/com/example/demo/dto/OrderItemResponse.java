package com.example.demo.dto;

import java.math.BigDecimal;

import com.example.demo.entity.OrderItem;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO позиции заказа в ответе API")
public record OrderItemResponse(
        @Schema(description = "ID позиции заказа", example = "1")
        Long id,
        @Schema(description = "ID блюда", example = "1")
        Long menuItemId,
        @Schema(description = "Название блюда", example = "Пицца Маргарита")
        String menuItemName,
        @Schema(description = "Количество", example = "2")
        Integer quantity,
        @Schema(description = "Цена за единицу", example = "15.99")
        BigDecimal price) {
    
    public static OrderItemResponse fromEntity(OrderItem orderItem) {
        return new OrderItemResponse(
                orderItem.getId(),
                orderItem.getMenuItem().getId(),
                orderItem.getMenuItem().getName(),
                orderItem.getQuantity(),
                orderItem.getPrice()
        );
    }
}