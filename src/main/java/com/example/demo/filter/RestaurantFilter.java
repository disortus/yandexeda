package com.example.demo.filter;

public class RestaurantFilter {
    private String nameSearch;

    public RestaurantFilter() {
    }

    public RestaurantFilter(String nameSearch) {
        this.nameSearch = nameSearch;
    }

    public String getNameSearch() {
        return nameSearch;
    }

    public void setNameSearch(String nameSearch) {
        this.nameSearch = nameSearch;
    }
}
