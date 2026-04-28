package com.example.demo.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dto.UserCreateRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.UserRepository;
import com.example.demo.utils.EmailUtils;
import com.example.demo.validator.UserValidator;

@Service
public class UserService {
    private final UserRepository repo;
    private final UserValidator validator;
    private final Clock clock;

    public UserService(UserRepository repo, UserValidator validator, Clock clock) {
        this.repo = repo;
        this.validator = validator;
        this.clock = clock;
    }

    public List<UserResponse> getAll() {
        return repo.findAll().stream().map(this::toResponse).toList();
    }

    public UserResponse getById(Long id) {
        User user = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        return toResponse(user);
    }

    public UserResponse create(UserCreateRequest request) {
        validator.validateForCreate(request);

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(EmailUtils.normalize(request.getEmail()));
        user.setCreatedAt(LocalDateTime.now(clock));

        User saved = repo.save(user);
        return toResponse(saved);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt());
    }
}
