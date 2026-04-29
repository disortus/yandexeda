package com.example.demo.entity.example;

import com.example.demo.enums.example.SimpleRoleExample;

public class SimpleUserEntityExample {
    private Long id;
    private String name;
    private String email;
    private SimpleRoleExample role;

    public SimpleUserEntityExample(Long id, String name, String email, SimpleRoleExample role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public SimpleRoleExample getRole() {
        return role;
    }
}
