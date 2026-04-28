package com.example.demo.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.UserResponse;
import com.example.demo.dto.auth.AuthResponse;
import com.example.demo.dto.auth.LoginRequest;
import com.example.demo.dto.auth.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.enums.TokenType;
import com.example.demo.enums.UserRole;
import com.example.demo.exception.UnauthorizedException;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtService;
import com.example.demo.utils.EmailUtils;
import com.example.demo.validator.UserValidator;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final Clock clock;

    public AuthService(
            UserRepository userRepository,
            UserValidator userValidator,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            Clock clock) {
        this.userRepository = userRepository;
        this.userValidator = userValidator;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.clock = clock;
    }

    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = EmailUtils.normalize(request.getEmail());
        userValidator.validateEmailUniqueness(normalizedEmail, request.getEmail());

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.CUSTOMER);
        user.setCreatedAt(LocalDateTime.now(clock));

        User saved = userRepository.save(user);
        return buildAuthResponse(saved);
    }

    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = EmailUtils.normalize(request.getEmail());
        User user = userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        String token = jwtService.generateAccessToken(user);
        LocalDateTime expiresAt = LocalDateTime.ofInstant(jwtService.extractExpiration(token), ZoneOffset.UTC);

        return new AuthResponse(
                token,
                TokenType.BEARER,
                expiresAt,
                new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getCreatedAt()));
    }
}