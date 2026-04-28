package com.example.demo.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на регистрацию CUSTOMER пользователя")
public class RegisterRequest {
    @Schema(description = "Имя пользователя", example = "Alice Johnson")
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @Schema(description = "Email пользователя", example = "alice@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 120, message = "Email must not exceed 120 characters")
    private String email;

    @Schema(description = "Пароль. Минимум 8 символов, 1 заглавная, 1 строчная, 1 цифра, 1 спецсимвол", example = "Qwerty123!")
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 72, message = "Password length must be between 8 and 72 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s]).+$", message = "Password must contain lowercase, uppercase, digit and special character")
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}