package com.example.demo.filter;

public class AddressFilter {
    private Long userId;
    private Long restaurantId;
    private String addressTextSearch;

    public AddressFilter() {
    }

    public AddressFilter(Long userId, Long restaurantId, String addressTextSearch) {
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.addressTextSearch = addressTextSearch;
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

    public String getAddressTextSearch() {
        return addressTextSearch;
    }

    public void setAddressTextSearch(String addressTextSearch) {
        this.addressTextSearch = addressTextSearch;
    }
}
