package com.example.demo.filter;

public class TagFilter {
    private String nameSearch;

    public TagFilter() {
    }

    public TagFilter(String nameSearch) {
        this.nameSearch = nameSearch;
    }

    public String getNameSearch() {
        return nameSearch;
    }

    public void setNameSearch(String nameSearch) {
        this.nameSearch = nameSearch;
    }
}
