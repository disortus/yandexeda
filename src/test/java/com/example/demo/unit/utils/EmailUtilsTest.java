package com.example.demo.unit.utils;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import com.example.demo.utils.EmailUtils;

class EmailUtilsTest {

    @Test
    void normalizeShouldTrimAndLowercase() {
        String normalized = EmailUtils.normalize("  Alice@Example.COM  ");

        assertThat(normalized).isEqualTo("alice@example.com");
    }

    @Test
    void normalizeShouldReturnNullForNullInput() {
        assertThat(EmailUtils.normalize(null)).isNull();
    }
}