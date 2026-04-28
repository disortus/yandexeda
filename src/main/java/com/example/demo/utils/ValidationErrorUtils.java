package com.example.demo.utils;

import java.util.stream.Collectors;

import org.springframework.validation.BindingResult;

public final class ValidationErrorUtils {
    private ValidationErrorUtils() {
    }

    public static String toFieldErrorMessage(BindingResult bindingResult) {
        String message = bindingResult.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        if (message.isBlank()) {
            return "Validation failed";
        }

        return message;
    }
}