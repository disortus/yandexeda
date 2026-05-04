package com.example.demo.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO для создания нового заказа")
public record OrderCreateRequest(
        @Schema(description = "ID клиента", example = "1")
        Long customerId,
        @Schema(description = "ID ресторана", example = "1")
        Long restaurantId,
        @Schema(description = "ID адреса доставки", example = "1")
        Long deliveryAddressId,
        @Schema(description = "Позиции заказа")
        List<OrderItemCreateRequest> items) {
}