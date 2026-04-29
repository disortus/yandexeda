package com.example.demo.exception;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.example.demo.dto.ApiErrorResponse;
import com.example.demo.dto.FieldValidationError;
import com.example.demo.enums.ApiErrorCode;

@Component
public class ApiErrorResponseFactory {
    private final Clock clock;

    public ApiErrorResponseFactory(Clock clock) {
        this.clock = clock;
    }

    public ApiErrorResponse create(HttpStatus status, ApiErrorCode code, String message, String path) {
        return create(status, code, message, path, List.of());
    }

    public ApiErrorResponse create(
            HttpStatus status,
            ApiErrorCode code,
            String message,
            String path,
            List<FieldValidationError> fieldErrors) {
        return new ApiErrorResponse(
                LocalDateTime.now(clock),
                status.value(),
                status.getReasonPhrase(),
                code,
                message,
                path,
                fieldErrors);
    }
}
