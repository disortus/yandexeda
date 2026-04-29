package com.example.demo.controller.example;

import com.example.demo.dto.example.SimpleGreetingRequestExample;
import com.example.demo.dto.example.SimpleGreetingResponseExample;
import com.example.demo.service.example.SimpleGreetingServiceExample;

public class SimpleGreetingControllerExample {
    private final SimpleGreetingServiceExample greetingService;

    public SimpleGreetingControllerExample(SimpleGreetingServiceExample greetingService) {
        this.greetingService = greetingService;
    }

    public SimpleGreetingResponseExample greet(String name) {
        SimpleGreetingRequestExample request = new SimpleGreetingRequestExample(name);
        return greetingService.greet(request);
    }
}
