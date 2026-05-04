package com.example.demo.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на регистрацию CUSTOMER пользователя")
public class RegisterRequest {
    @Schema(description = "Имя пользователя", example = "alice")
    @NotBlank(message = "Username is required")
    @Size(min = 2, max = 50, message = "Username must be between 2 and 50 characters")
    private String username;

    @Schema(description = "Телефон пользователя (10 цифр)", example = "7012345678")
    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\d{10}$", message = "Phone must contain exactly 10 digits")
    private String phone;

    @Schema(description = "Пароль. Минимум 8 символов, 1 заглавная, 1 строчная, 1 цифра, 1 спецсимвол", example = "Qwerty123!")
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 72, message = "Password length must be between 8 and 72 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s]).+$", message = "Password must contain lowercase, uppercase, digit and special character")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}