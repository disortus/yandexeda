package com.example.demo.exception.example;

public class SimpleExceptionMapperExample {

    public SimpleErrorResponseExample toError(RuntimeException exception) {
        return new SimpleErrorResponseExample("ERROR", exception.getMessage());
    }
}
