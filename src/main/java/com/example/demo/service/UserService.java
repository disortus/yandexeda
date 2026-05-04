package com.example.demo.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.UserCreateRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.UserRepository;
import com.example.demo.validator.UserValidator;

@Service
public class UserService {
    private final UserRepository repo;
    private final UserValidator validator;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    public UserService(UserRepository repo, UserValidator validator, PasswordEncoder passwordEncoder, Clock clock) {
        this.repo = repo;
        this.validator = validator;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        return repo.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        User user = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        return toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getByPhone(String phone) {
        User user = repo.findByPhone(phone)
                .orElseThrow(() -> new ResourceNotFoundException("User with phone '" + phone + "' not found"));

        return toResponse(user);
    }

    @Transactional
    public UserResponse createByAdmin(UserCreateRequest request) {
        String phone = request.getPhone().trim();
        validator.validatePhoneUniqueness(phone);

        User user = new User();
        user.setUsername(request.getUsername().trim());
        user.setPhone(phone);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setCreatedAt(LocalDateTime.now(clock));

        User saved = repo.save(user);
        return toResponse(saved);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getPhone(),
                user.getRole(),
                user.getCreatedAt());
    }
}
