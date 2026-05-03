package com.example.demo.filter;

public class AddressFilter {
    private Long userId;
    private String addressTextSearch;

    public AddressFilter() {
    }

    public AddressFilter(Long userId, String addressTextSearch) {
        this.userId = userId;
        this.addressTextSearch = addressTextSearch;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAddressTextSearch() {
        return addressTextSearch;
    }

    public void setAddressTextSearch(String addressTextSearch) {
        this.addressTextSearch = addressTextSearch;
    }
}
