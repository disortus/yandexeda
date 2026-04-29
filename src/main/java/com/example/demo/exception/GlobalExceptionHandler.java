package com.example.demo.exception;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.example.demo.dto.ApiErrorResponse;
import com.example.demo.dto.FieldValidationError;
import com.example.demo.enums.ApiErrorCode;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final ApiErrorResponseFactory errorResponseFactory;

    public GlobalExceptionHandler(ApiErrorResponseFactory errorResponseFactory) {
        this.errorResponseFactory = errorResponseFactory;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, ApiErrorCode.RESOURCE_NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleAlreadyExists(ResourceAlreadyExistsException ex,
            HttpServletRequest request) {
        return buildError(HttpStatus.CONFLICT, ApiErrorCode.RESOURCE_ALREADY_EXISTS, ex.getMessage(),
                request.getRequestURI());
    }

    @ExceptionHandler(BusinessValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessValidation(BusinessValidationException ex,
            HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, ApiErrorCode.BUSINESS_VALIDATION, ex.getMessage(),
                request.getRequestURI());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiErrorResponse> handleUnauthorized(UnauthorizedException ex, HttpServletRequest request) {
        return buildError(HttpStatus.UNAUTHORIZED, ApiErrorCode.AUTHENTICATION_FAILED, ex.getMessage(),
                request.getRequestURI());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        return buildError(HttpStatus.FORBIDDEN, ApiErrorCode.ACCESS_DENIED,
                "You do not have enough permissions for this action", request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        List<FieldValidationError> fieldErrors = ValidationErrorUtils.toFieldErrors(ex.getBindingResult());
        String message = ValidationErrorUtils.toFieldErrorMessage(fieldErrors);

        return buildError(HttpStatus.BAD_REQUEST, ApiErrorCode.REQUEST_VALIDATION, message, request.getRequestURI(),
                fieldErrors);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {
        String message = "Parameter '" + ex.getName() + "' has invalid value '" + ex.getValue() + "'";
        return buildError(HttpStatus.BAD_REQUEST, ApiErrorCode.REQUEST_VALIDATION, message, request.getRequestURI());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex,
            HttpServletRequest request) {
        log.warn("Database integrity constraint violation", ex);
        return buildError(HttpStatus.CONFLICT, ApiErrorCode.RESOURCE_ALREADY_EXISTS,
                "Resource violates unique or integrity constraint", request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnknown(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception", ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, ApiErrorCode.INTERNAL_ERROR, "Unexpected server error",
                request.getRequestURI());
    }

    private ResponseEntity<ApiErrorResponse> buildError(
            HttpStatus status,
            ApiErrorCode code,
            String message,
            String path) {
        return buildError(status, code, message, path, List.of());
    }

    private ResponseEntity<ApiErrorResponse> buildError(
            HttpStatus status,
            ApiErrorCode code,
            String message,
            String path,
            List<FieldValidationError> fieldErrors) {
        ApiErrorResponse response = errorResponseFactory.create(status, code, message, path, fieldErrors);

        return ResponseEntity.status(status).body(response);
    }
}