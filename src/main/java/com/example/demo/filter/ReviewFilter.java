package com.example.demo.filter;

public class ReviewFilter {
    private Long userId;
    private Long restaurantId;
    private Long menuItemId;
    private Integer minRating;
    private Integer maxRating;

    public ReviewFilter() {
    }

    public ReviewFilter(Long userId, Long restaurantId, Long menuItemId, Integer minRating, Integer maxRating) {
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.menuItemId = menuItemId;
        this.minRating = minRating;
        this.maxRating = maxRating;
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

    public Integer getMinRating() {
        return minRating;
    }

    public void setMinRating(Integer minRating) {
        this.minRating = minRating;
    }

    public Integer getMaxRating() {
        return maxRating;
    }

    public void setMaxRating(Integer maxRating) {
        this.maxRating = maxRating;
    }
}
