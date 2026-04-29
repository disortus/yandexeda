package com.example.demo.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Clock;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.demo.controller.UserController;
import com.example.demo.dto.UserResponse;
import com.example.demo.enums.ApiErrorCode;
import com.example.demo.enums.UserRole;
import com.example.demo.exception.ApiErrorResponseFactory;
import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserControllerWebMvcTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService))
                .setControllerAdvice(new GlobalExceptionHandler(new ApiErrorResponseFactory(Clock.systemUTC())))
                .build();
    }

    @Test
    void getByIdShouldReturnUserResponse() throws Exception {
        when(userService.getById(42L)).thenReturn(new UserResponse(
                42L,
                "Alice",
                "alice@example.com",
                UserRole.CUSTOMER,
                LocalDateTime.parse("2026-04-28T20:00:00")));

        mockMvc.perform(get("/users/42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.role").value(UserRole.CUSTOMER.name()));
    }

    @Test
    void createShouldReturnCreatedUserResponse() throws Exception {
        when(userService.createByAdmin(any())).thenReturn(new UserResponse(
                10L,
                "Courier",
                "courier@example.com",
                UserRole.COURIER,
                LocalDateTime.parse("2026-04-28T21:00:00")));

        String payload = """
                {
                  "name": "Courier",
                  "email": "courier@example.com",
                  "password": "Qwerty123!",
                  "role": "COURIER"
                }
                """;

        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.email").value("courier@example.com"))
                .andExpect(jsonPath("$.role").value(UserRole.COURIER.name()));
    }

    @Test
    void createShouldReturnStructuredValidationErrorForInvalidPayload() throws Exception {
        String payload = """
                {
                  "name": "",
                  "email": "bad-email",
                  "password": "123",
                  "role": null
                }
                """;

        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ApiErrorCode.REQUEST_VALIDATION.name()))
                .andExpect(jsonPath("$.fieldErrors").isArray())
                .andExpect(jsonPath("$.fieldErrors").isNotEmpty());
    }
}
