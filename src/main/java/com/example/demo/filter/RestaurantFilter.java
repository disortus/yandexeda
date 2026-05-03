package com.example.demo.filter;

public class RestaurantFilter {
    private String city;
    private Double minRating;
    private String tag;
    private String nameSearch;

    public RestaurantFilter() {
    }

    public RestaurantFilter(String city, Double minRating, String tag, String nameSearch) {
        this.city = city;
        this.minRating = minRating;
        this.tag = tag;
        this.nameSearch = nameSearch;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Double getMinRating() {
        return minRating;
    }

    public void setMinRating(Double minRating) {
        this.minRating = minRating;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getNameSearch() {
        return nameSearch;
    }

    public void setNameSearch(String nameSearch) {
        this.nameSearch = nameSearch;
    }
}
