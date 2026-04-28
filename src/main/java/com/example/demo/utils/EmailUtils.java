package com.example.demo.utils;

public final class EmailUtils {
    private EmailUtils() {
    }

    public static String normalize(String email) {
        if (email == null) {
            return null;
        }
        return email.trim().toLowerCase();
    }
}