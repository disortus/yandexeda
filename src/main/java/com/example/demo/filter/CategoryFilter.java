package com.example.demo.filter;

public class CategoryFilter {
    private String nameSearch;

    public CategoryFilter() {
    }

    public CategoryFilter(String nameSearch) {
        this.nameSearch = nameSearch;
    }

    public String getNameSearch() {
        return nameSearch;
    }

    public void setNameSearch(String nameSearch) {
        this.nameSearch = nameSearch;
    }
}
