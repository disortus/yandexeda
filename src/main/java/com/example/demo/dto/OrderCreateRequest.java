package com.example.demo.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;

@Schema(description = "DTO для создания нового заказа")
public record OrderCreateRequest(
        @Schema(description = "ID ресторана", example = "1")
        Long restaurantId,
        @Schema(description = "ID адреса доставки", example = "1")
        Long deliveryAddressId,
        @Valid
        @Schema(description = "Новый адрес доставки (если deliveryAddressId не передан)")
        OrderDeliveryAddressRequest deliveryAddress,
        @Schema(description = "Позиции заказа")
        List<OrderItemCreateRequest> items) {
}