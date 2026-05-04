package com.example.demo.utils;
public final class NumberUtils {
    private NumberUtils() {}
    public static Integer tryParseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return null;
        }
    }
    public static Long tryParseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            return null;
        }
    }
}
