package com.example.demo.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Запрос на вход в систему")
public class LoginRequest {
    @Schema(description = "Телефон пользователя (10 цифр)", example = "7012345678")
    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\d{10}$", message = "Phone must contain exactly 10 digits")
    private String phone;

    @Schema(description = "Пароль пользователя", example = "Qwerty123!")
    @NotBlank(message = "Password is required")
    private String password;

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