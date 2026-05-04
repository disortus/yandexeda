package com.example.demo.utils;

public final class StringUtils {
    private StringUtils() {}

    public static String safeTrim(String value) {
        if (value == null) return "";
        return value.trim();
    }

    public static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }
    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
