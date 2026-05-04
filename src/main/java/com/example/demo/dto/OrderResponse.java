package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.entity.Order;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO заказа в ответе API")
public record OrderResponse(
        @Schema(description = "ID заказа", example = "1")
        Long id,
        @Schema(description = "ID клиента", example = "1")
        Long customerId,
        @Schema(description = "Имя клиента", example = "Иван")
        String customerName,
        @Schema(description = "ID курьера", example = "2")
        Long courierId,
        @Schema(description = "Имя курьера", example = "Петр")
        String courierName,
        @Schema(description = "ID ресторана", example = "1")
        Long restaurantId,
        @Schema(description = "Название ресторана", example = "Пицца Марго")
        String restaurantName,
        @Schema(description = "Адрес доставки")
        AddressResponse deliveryAddress,
        @Schema(description = "Позиции заказа")
        List<OrderItemResponse> items,
        @Schema(description = "Общая стоимость", example = "35.98")
        BigDecimal totalPrice,
        @Schema(description = "Статус заказа", example = "PENDING")
        String status,
        @Schema(description = "Дата создания заказа", example = "2026-04-28T20:45:10")
        LocalDateTime createdAt) {
    
    public static OrderResponse fromEntity(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomer().getId(),
                order.getCustomer().getUsername(),
                order.getCourier() != null ? order.getCourier().getId() : null,
                order.getCourier() != null ? order.getCourier().getUsername() : null,
                order.getRestaurant().getId(),
                order.getRestaurant().getName(),
                order.getDeliveryAddress() != null ? AddressResponse.fromEntity(order.getDeliveryAddress()) : null,
                order.getOrderItems() != null ? 
                    order.getOrderItems().stream()
                        .map(OrderItemResponse::fromEntity)
                        .toList() : null,
                order.getTotalPrice(),
                order.getStatus().name(),
                order.getCreatedAt()
        );
    }
}