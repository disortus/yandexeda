package com.example.demo.service.example;

import com.example.demo.dto.example.SimpleGreetingRequestExample;
import com.example.demo.dto.example.SimpleGreetingResponseExample;
import com.example.demo.utils.example.SimpleTextUtilsExample;
import com.example.demo.validator.example.SimpleNameValidatorExample;

public class SimpleGreetingServiceExample {
    private final SimpleNameValidatorExample validator;

    public SimpleGreetingServiceExample(SimpleNameValidatorExample validator) {
        this.validator = validator;
    }

    public SimpleGreetingResponseExample greet(SimpleGreetingRequestExample request) {
        String name = SimpleTextUtilsExample.normalizeName(request.name());
        validator.validate(name);
        return new SimpleGreetingResponseExample("Hello, " + name);
    }
}
