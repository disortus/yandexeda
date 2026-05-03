package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Запрос на создание заказа")
public class OrderCreateRequest {
    @Schema(description = "ID покупателя", example = "1")
    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @Schema(description = "ID курьера", example = "2")
    @NotNull(message = "Courier ID is required")
    private Long courierId;

    @Schema(description = "ID ресторана", example = "1")
    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;

    @Schema(description = "ID адреса доставки", example = "1")
    @NotNull(message = "Delivery address ID is required")
    private Long deliveryAddressId;

    @Schema(description = "Статус заказа", example = "pending", allowableValues = { "pending", "confirmed", "preparing", "ready", "out_for_delivery", "delivered", "cancelled" })
    @NotNull(message = "Status is required")
    private String status;

    @Schema(description = "Итоговая стоимость заказа", example = "850.75")
    @NotNull(message = "Total price is required")
    @DecimalMin(value = "0.01", message = "Total price must be greater than 0")
    private BigDecimal totalPrice;

    public OrderCreateRequest() {
    }

    public OrderCreateRequest(Long customerId, Long courierId, Long restaurantId, Long deliveryAddressId, String status, BigDecimal totalPrice) {
        this.customerId = customerId;
        this.courierId = courierId;
        this.restaurantId = restaurantId;
        this.deliveryAddressId = deliveryAddressId;
        this.status = status;
        this.totalPrice = totalPrice;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getCourierId() {
        return courierId;
    }

    public void setCourierId(Long courierId) {
        this.courierId = courierId;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Long getDeliveryAddressId() {
        return deliveryAddressId;
    }

    public void setDeliveryAddressId(Long deliveryAddressId) {
        this.deliveryAddressId = deliveryAddressId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
