package com.example.demo.unit.service;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.dto.auth.AuthResponse;
import com.example.demo.dto.auth.LoginRequest;
import com.example.demo.dto.auth.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.enums.UserRole;
import com.example.demo.exception.UnauthorizedException;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtService;
import com.example.demo.service.AuthService;
import com.example.demo.validator.UserValidator;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserValidator userValidator;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private Clock clock;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerShouldCreateCustomerAndReturnJwt() {
        when(clock.instant()).thenReturn(Instant.parse("2026-04-28T20:00:00Z"));
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);

        RegisterRequest request = new RegisterRequest();
        request.setUsername("Alice");
        request.setPhone("7012345678");
        request.setPassword("Qwerty123!");

        when(passwordEncoder.encode("Qwerty123!")).thenReturn("hash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(jwtService.generateAccessToken(any(User.class))).thenReturn("jwt-token");
        when(jwtService.extractExpiration("jwt-token")).thenReturn(Instant.parse("2026-04-28T21:00:00Z"));

        AuthResponse response = authService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User saved = userCaptor.getValue();

        assertThat(saved.getPhone()).isEqualTo("7012345678");
        assertThat(saved.getRole()).isEqualTo(UserRole.CUSTOMER);
        assertThat(saved.getPasswordHash()).isEqualTo("hash");

        assertThat(response.accessToken()).isEqualTo("jwt-token");
        assertThat(response.user().phone()).isEqualTo("7012345678");
        assertThat(response.user().role()).isEqualTo(UserRole.CUSTOMER);
    }

    @Test
    void loginShouldReturnTokenWhenCredentialsAreValid() {
        LoginRequest request = new LoginRequest();
        request.setPhone("7012345678");
        request.setPassword("Qwerty123!");

        User user = new User("Alice", "7012345678", "hash", UserRole.CUSTOMER, LocalDateTime.now());
        user.setId(1L);

        when(userRepository.findByPhone("7012345678")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Qwerty123!", "hash")).thenReturn(true);
        when(jwtService.generateAccessToken(user)).thenReturn("jwt-token");
        when(jwtService.extractExpiration("jwt-token")).thenReturn(Instant.parse("2026-04-28T21:00:00Z"));

        AuthResponse response = authService.login(request);

        assertThat(response.accessToken()).isEqualTo("jwt-token");
        assertThat(response.user().id()).isEqualTo(1L);
    }

    @Test
    void loginShouldThrowWhenPasswordDoesNotMatch() {
        LoginRequest request = new LoginRequest();
        request.setPhone("7012345678");
        request.setPassword("WrongPass123!");

        User user = new User("Alice", "7012345678", "hash", UserRole.CUSTOMER, LocalDateTime.now());

        when(userRepository.findByPhone("7012345678")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPass123!", "hash")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid credentials");
    }
}