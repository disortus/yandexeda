package com.example.demo.dto;

import com.example.demo.entity.Address;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO адреса в ответе API")
public record AddressResponse(
        @Schema(description = "ID адреса", example = "1")
        Long id,
        @Schema(description = "ID пользователя", example = "1")
        Long userId,
        @Schema(description = "ID ресторана", example = "1")
        Long restaurantId,
        @Schema(description = "Текст адреса", example = "ул. Ленина, дом 5, кв. 12")
        String addressText) {
    
    public static AddressResponse fromEntity(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getUser() != null ? address.getUser().getId() : null,
                address.getRestaurant() != null ? address.getRestaurant().getId() : null,
                address.getAddressText()
        );
    }
}
