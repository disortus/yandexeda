package com.example.demo.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на создание блюда в меню")
public class MenuItemCreateRequest {
    @Schema(description = "ID ресторана", example = "1")
    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;

    @Schema(description = "ID категории", example = "1")
    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @Schema(description = "Название блюда", example = "Маргарита")
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Schema(description = "Описание блюда", example = "Классическая пицца с моцареллой и томатами")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Schema(description = "Цена блюда", example = "450.50")
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    public MenuItemCreateRequest() {
    }

    public MenuItemCreateRequest(Long restaurantId, Long categoryId, String name, String description, BigDecimal price) {
        this.restaurantId = restaurantId;
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
