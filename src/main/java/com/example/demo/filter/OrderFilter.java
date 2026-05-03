package com.example.demo.filter;

public class OrderFilter {
    private Long restaurantId;
    private String status;
    private Double minTotalPrice;
    private Double maxTotalPrice;

    public OrderFilter() {
    }

    public OrderFilter(Long restaurantId, String status, Double minTotalPrice, Double maxTotalPrice) {
        this.restaurantId = restaurantId;
        this.status = status;
        this.minTotalPrice = minTotalPrice;
        this.maxTotalPrice = maxTotalPrice;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getMinTotalPrice() {
        return minTotalPrice;
    }

    public void setMinTotalPrice(Double minTotalPrice) {
        this.minTotalPrice = minTotalPrice;
    }

    public Double getMaxTotalPrice() {
        return maxTotalPrice;
    }

    public void setMaxTotalPrice(Double maxTotalPrice) {
        this.maxTotalPrice = maxTotalPrice;
    }
}

    public OrderFilter() {
    }

    public OrderFilter(String status, Long customerId, Long courierId, Long restaurantId, LocalDateTime dateFrom, LocalDateTime dateTo) {
        this.status = status;
        this.customerId = customerId;
        this.courierId = courierId;
        this.restaurantId = restaurantId;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getCourierId() {
        return courierId;
    }

    public void setCourierId(Long courierId) {
        this.courierId = courierId;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public LocalDateTime getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(LocalDateTime dateFrom) {
        this.dateFrom = dateFrom;
    }

    public LocalDateTime getDateTo() {
        return dateTo;
    }

    public void setDateTo(LocalDateTime dateTo) {
        this.dateTo = dateTo;
    }
}
