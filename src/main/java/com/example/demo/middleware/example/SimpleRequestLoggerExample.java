package com.example.demo.middleware.example;

public class SimpleRequestLoggerExample {

    public String format(SimpleRequestLogExample log) {
        return log.method() + " " + log.path() + " -> " + log.status() + " (" + log.durationMs() + " ms)";
    }
}
