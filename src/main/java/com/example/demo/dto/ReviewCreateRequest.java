package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на создание отзыва")
public class ReviewCreateRequest {
    @Schema(description = "ID пользователя", example = "1")
    @NotNull(message = "User ID is required")
    private Long userId;

    @Schema(description = "ID ресторана", example = "1")
    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;

    @Schema(description = "ID блюда", example = "1")
    @NotNull(message = "Menu item ID is required")
    private Long menuItemId;

    @Schema(description = "Рейтинг (1-5)", example = "5")
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer rating;

    @Schema(description = "Комментарий", example = "Отличное блюдо, рекомендую!")
    @Size(max = 500, message = "Comment must not exceed 500 characters")
    private String comment;

    public ReviewCreateRequest() {
    }

    public ReviewCreateRequest(Long userId, Long restaurantId, Long menuItemId, Integer rating, String comment) {
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.menuItemId = menuItemId;
        this.rating = rating;
        this.comment = comment;
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

    public Long getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(Long menuItemId) {
        this.menuItemId = menuItemId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
