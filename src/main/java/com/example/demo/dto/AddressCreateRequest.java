package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Запрос на создание адреса доставки")
public class AddressCreateRequest {
    @Schema(description = "ID пользователя", example = "1")
    @NotNull(message = "User ID is required")
    private Long userId;

    @Schema(description = "ID ресторана", example = "1")
    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;

    @Schema(description = "Текст адреса", example = "ул. Ленина, дом 5, кв. 12")
    @NotBlank(message = "Address text is required")
    private String addressText;

    public AddressCreateRequest() {
    }

    public AddressCreateRequest(Long userId, Long restaurantId, String addressText) {
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.addressText = addressText;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getAddressText() {
        return addressText;
    }

    public void setAddressText(String addressText) {
        this.addressText = addressText;
    }
}
