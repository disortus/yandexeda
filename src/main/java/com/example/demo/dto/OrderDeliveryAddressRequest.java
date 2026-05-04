package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Новый адрес доставки внутри создания заказа")
public class OrderDeliveryAddressRequest {
    @Schema(description = "Текст адреса", example = "Астана, Кабанбай батыра, 10")
    @NotBlank(message = "Address text is required")
    private String addressText;

    @Schema(description = "Широта", example = "51.123")
    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    private Double lat;

    @Schema(description = "Долгота", example = "71.456")
    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    private Double lng;

    public OrderDeliveryAddressRequest() {
    }

    public OrderDeliveryAddressRequest(String addressText, Double lat, Double lng) {
        this.addressText = addressText;
        this.lat = lat;
        this.lng = lng;
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