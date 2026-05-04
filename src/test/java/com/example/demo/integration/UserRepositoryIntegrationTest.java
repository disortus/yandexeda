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
    void existsByPhoneShouldWork() {
        User user = new User(
                "Alice",
                "7012345678",
                passwordEncoder.encode("Qwerty123!"),
                UserRole.CUSTOMER,
                LocalDateTime.now());
        userRepository.save(user);

        assertThat(userRepository.existsByPhone("7012345678")).isTrue();
        assertThat(userRepository.existsByPhone("7000000000")).isFalse();
    }

    @Test
    void findByPhoneShouldReturnUser() {
        User user = new User(
                "Courier",
                "7011111111",
                passwordEncoder.encode("Qwerty123!"),
                UserRole.COURIER,
                LocalDateTime.now());
        userRepository.save(user);

        assertThat(userRepository.findByPhone("7011111111"))
                .isPresent()
                .get()
                .extracting(User::getRole)
                .isEqualTo(UserRole.COURIER);
    }
}