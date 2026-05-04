package com.example.demo.filter;

import com.example.demo.enums.UserRole;

public class UserFilter {
    private String userNameSearch;
    private String phoneSearch;
    private UserRole role;

    public UserFilter() {
    }

    public UserFilter(String userNameSearch, String phoneSearch, UserRole role) {
        this.userNameSearch = userNameSearch;
        this.phoneSearch = phoneSearch;
        this.role = role;
    }

    public String getUserNameSearch() {
        return userNameSearch;
    }

    public void setUserNameSearch(String userNameSearch) {
        this.userNameSearch = userNameSearch;
    }

    public String getPhoneSearch() {
        return phoneSearch;
    }

    public void setPhoneSearch(String phoneSearch) {
        this.phoneSearch = phoneSearch;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
