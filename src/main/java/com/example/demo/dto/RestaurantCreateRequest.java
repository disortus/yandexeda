package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на создание ресторана")
public class RestaurantCreateRequest {
    @Schema(description = "Название ресторана", example = "Пицца Марго")
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Schema(description = "Описание ресторана", example = "Итальянская кухня, пицца на тонком тесте")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    public RestaurantCreateRequest() {
    }

    public RestaurantCreateRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
