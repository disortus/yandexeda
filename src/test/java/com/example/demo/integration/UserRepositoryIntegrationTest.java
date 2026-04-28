package com.example.demo.integration;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import com.example.demo.entity.User;
import com.example.demo.enums.UserRole;
import com.example.demo.repository.UserRepository;

@SpringBootTest
@ActiveProfiles("test")
class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    void existsByEmailIgnoreCaseShouldWorkCaseInsensitive() {
        User user = new User(
                "Alice",
                "alice@example.com",
                passwordEncoder.encode("Qwerty123!"),
                UserRole.CUSTOMER,
                LocalDateTime.now());
        userRepository.save(user);

        assertThat(userRepository.existsByEmailIgnoreCase("ALICE@EXAMPLE.COM")).isTrue();
        assertThat(userRepository.existsByEmailIgnoreCase("bob@example.com")).isFalse();
    }

    @Test
    void findByEmailIgnoreCaseShouldReturnUser() {
        User user = new User(
                "Courier",
                "courier@example.com",
                passwordEncoder.encode("Qwerty123!"),
                UserRole.COURIER,
                LocalDateTime.now());
        userRepository.save(user);

        assertThat(userRepository.findByEmailIgnoreCase("COURIER@example.com"))
                .isPresent()
                .get()
                .extracting(User::getRole)
                .isEqualTo(UserRole.COURIER);
    }
}