package com.example.demo.config.example;

public record SimpleAppSettingsExample(
        String applicationName,
        int serverPort,
        boolean swaggerEnabled) {

    public static SimpleAppSettingsExample localDefaults() {
        return new SimpleAppSettingsExample("demo", 8080, true);
    }
}
