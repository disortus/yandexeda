package com.example.demo.unit.validator;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.exception.BusinessValidationException;
import com.example.demo.repository.UserRepository;
import com.example.demo.validator.UserValidator;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserValidator userValidator;

    @Test
    void validateEmailUniquenessShouldThrowWhenEmailExists() {
        when(userRepository.existsByEmailIgnoreCase("alice@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userValidator.validateEmailUniqueness("alice@example.com", "Alice@Example.com"))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void validateEmailUniquenessShouldPassWhenEmailNotExists() {
        when(userRepository.existsByEmailIgnoreCase("alice@example.com")).thenReturn(false);

        assertThatCode(() -> userValidator.validateEmailUniqueness("alice@example.com", "alice@example.com"))
                .doesNotThrowAnyException();
    }
}