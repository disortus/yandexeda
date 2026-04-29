package com.example.demo.exception;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.example.demo.dto.FieldValidationError;

public final class ValidationErrorUtils {
    private ValidationErrorUtils() {
    }

    public static List<FieldValidationError> toFieldErrors(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .sorted(Comparator.comparing(FieldError::getField)
                        .thenComparing(error -> String.valueOf(error.getDefaultMessage())))
                .map(error -> new FieldValidationError(error.getField(), resolveMessage(error)))
                .toList();
    }

    public static String toFieldErrorMessage(List<FieldValidationError> fieldErrors) {
        String message = fieldErrors.stream()
                .map(error -> error.field() + ": " + error.message())
                .collect(Collectors.joining("; "));

        if (message.isBlank()) {
            return "Validation failed";
        }

        return message;
    }

    private static String resolveMessage(FieldError error) {
        String defaultMessage = error.getDefaultMessage();
        if (defaultMessage == null || defaultMessage.isBlank()) {
            return "Invalid value";
        }

        return defaultMessage;
    }
}

