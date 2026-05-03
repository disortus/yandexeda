package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Запрос на обновление адреса")
public class AddressUpdateRequest {
    @Schema(description = "Текст адреса", example = "ул. Ленина, дом 5, кв. 12")
    private String addressText;

    public AddressUpdateRequest() {
    }

    public AddressUpdateRequest(String addressText) {
        this.addressText = addressText;
    }

    public String getAddressText() {
        return addressText;
    }

    public void setAddressText(String addressText) {
        this.addressText = addressText;
    }
}
