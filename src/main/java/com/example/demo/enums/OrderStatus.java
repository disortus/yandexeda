package com.example.demo.enums;

public enum OrderStatus {
    PENDING,
    ACCEPTED,
    DELIVERING,
    COMPLETED,
    CANCELLED;

    public String asAuthority() {
        return "ROLE_" + name();
    }
}