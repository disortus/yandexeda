package com.example.demo.unit.validator;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.exception.ResourceAlreadyExistsException;
import com.example.demo.repository.UserRepository;
import com.example.demo.validator.UserValidator;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserValidator userValidator;

    @Test
    void validatePhoneUniquenessShouldThrowWhenPhoneExists() {
        when(userRepository.existsByPhone("7012345678")).thenReturn(true);

        assertThatThrownBy(() -> userValidator.validatePhoneUniqueness("7012345678"))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void validatePhoneUniquenessShouldPassWhenPhoneNotExists() {
        when(userRepository.existsByPhone("7012345678")).thenReturn(false);

        assertThatCode(() -> userValidator.validatePhoneUniqueness("7012345678"))
                .doesNotThrowAnyException();
    }
}