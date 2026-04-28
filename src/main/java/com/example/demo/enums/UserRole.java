package com.example.demo.enums;

public enum UserRole {
    CUSTOMER,
    ADMIN,
    COURIER;

    public String asAuthority() {
        return "ROLE_" + name();
    }
}