package com.example.demo.enums.example;

public enum SimpleRoleExample {
    READER,
    MANAGER;

    public boolean canReadUsers() {
        return this == MANAGER;
    }
}
