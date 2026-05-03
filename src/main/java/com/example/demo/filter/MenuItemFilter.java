package com.example.demo.filter;

public class MenuItemFilter {
    private String category;
    private Double minPrice;
    private Double maxPrice;
    private Long restaurantId;
    private Boolean isAvailable;

    public MenuItemFilter() {
    }

    public MenuItemFilter(String category, Double minPrice, Double maxPrice, Long restaurantId, Boolean isAvailable) {
        this.category = category;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.restaurantId = restaurantId;
        this.isAvailable = isAvailable;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
