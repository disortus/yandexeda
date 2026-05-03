package com.example.demo.filter;

public class MenuItemFilter {
    private Long categoryId;
    private Double minPrice;
    private Double maxPrice;
    private Long restaurantId;
    private Boolean isAvailable;

    public MenuItemFilter() {
    }

    public MenuItemFilter(Long categoryId, Double minPrice, Double maxPrice, Long restaurantId, Boolean isAvailable) {
        this.categoryId = categoryId;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.restaurantId = restaurantId;
        this.isAvailable = isAvailable;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
}
