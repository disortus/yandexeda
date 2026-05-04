package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Запрос на создание адреса доставки")
public class AddressCreateRequest {
    @Schema(description = "ID ресторана (для адреса ресторана; опционально)", example = "1")
    private Long restaurantId;

    @Schema(description = "Текст адреса", example = "ул. Ленина, дом 5, кв. 12")
    @NotBlank(message = "Address text is required")
    private String addressText;

    @Schema(description = "Широта точки доставки", example = "51.123")
    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    private Double lat;

    @Schema(description = "Долгота точки доставки", example = "71.456")
    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    private Double lng;

    public AddressCreateRequest() {
    }

    public AddressCreateRequest(Long restaurantId, String addressText, Double lat, Double lng) {
        this.restaurantId = restaurantId;
        this.addressText = addressText;
        this.lat = lat;
        this.lng = lng;
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

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
