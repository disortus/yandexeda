package com.example.demo.validator.example;

import com.example.demo.exception.example.SimpleValidationExceptionExample;

public class SimpleNameValidatorExample {

    public void validate(String name) {
        if (name == null || name.isBlank()) {
            throw new SimpleValidationExceptionExample("Name is required");
        }
    }
}
