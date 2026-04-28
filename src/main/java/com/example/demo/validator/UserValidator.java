package com.example.demo.validator;

import org.springframework.stereotype.Component;

import com.example.demo.dto.UserCreateRequest;
import com.example.demo.exception.BusinessValidationException;
import com.example.demo.repository.UserRepository;
import com.example.demo.utils.EmailUtils;

@Component
public class UserValidator {
    private final UserRepository repo;

    public UserValidator(UserRepository repo) {
        this.repo = repo;
    }

    public void validateForCreate(UserCreateRequest request) {
        String normalizedEmail = EmailUtils.normalize(request.getEmail());
        if (repo.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new BusinessValidationException("User with email '" + request.getEmail() + "' already exists");
        }
    }
}