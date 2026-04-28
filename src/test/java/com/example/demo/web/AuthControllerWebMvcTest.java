package com.example.demo.web;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.demo.controller.AuthController;
import com.example.demo.dto.UserResponse;
import com.example.demo.dto.auth.AuthResponse;
import com.example.demo.enums.TokenType;
import com.example.demo.enums.UserRole;
import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.service.AuthService;

@ExtendWith(MockitoExtension.class)
class AuthControllerWebMvcTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(authService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void registerShouldReturnCreatedWithToken() throws Exception {
        AuthResponse authResponse = new AuthResponse(
                "jwt-token",
                TokenType.BEARER,
                LocalDateTime.parse("2026-04-28T21:00:00"),
                new UserResponse(1L, "Alice", "alice@example.com", UserRole.CUSTOMER,
                        LocalDateTime.parse("2026-04-28T20:00:00")));

        when(authService.register(any())).thenReturn(authResponse);

        String payload = """
                {
                  "name": "Alice",
                  "email": "alice@example.com",
                  "password": "Qwerty123!"
                }
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("jwt-token"))
                .andExpect(jsonPath("$.tokenType").value("BEARER"))
                .andExpect(jsonPath("$.user.email").value("alice@example.com"));
    }

    @Test
    void registerShouldReturnBadRequestForInvalidPayload() throws Exception {
        String payload = """
                {
                  "name": "",
                  "email": "bad-email",
                  "password": "123"
                }
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }
}