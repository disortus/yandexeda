package com.example.demo.validator;

import org.springframework.stereotype.Component;

import com.example.demo.exception.ResourceAlreadyExistsException;
import com.example.demo.repository.UserRepository;

@Component
public class UserValidator {
    private final UserRepository repo;

    public UserValidator(UserRepository repo) {
        this.repo = repo;
    }

    public void validatePhoneUniqueness(String phone) {
        if (repo.existsByPhone(phone)) {
            throw new ResourceAlreadyExistsException("User with phone '" + phone + "' already exists");
        }
    }
}