package com.example.demo.validator;

import org.springframework.stereotype.Component;

import com.example.demo.exception.BusinessValidationException;
import com.example.demo.repository.UserRepository;

@Component
public class UserValidator {
    private final UserRepository repo;

    public UserValidator(UserRepository repo) {
        this.repo = repo;
    }

    public void validateEmailUniqueness(String normalizedEmail, String originalEmail) {
        if (repo.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new BusinessValidationException("User with email '" + originalEmail + "' already exists");
        }
    }
}