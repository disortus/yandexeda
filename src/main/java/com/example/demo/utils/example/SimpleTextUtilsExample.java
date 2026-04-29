package com.example.demo.utils.example;

public final class SimpleTextUtilsExample {
    private SimpleTextUtilsExample() {
    }

    public static String normalizeName(String value) {
        if (value == null) {
            return "";
        }

        return value.trim();
    }
}
