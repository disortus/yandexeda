package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.StatusResponse;

@RestController
@RequestMapping("/status")
public class StatusController {
    private final String applicationName;

    public StatusController(@Value("${spring.application.name}") String applicationName) {
        this.applicationName = applicationName;
    }

    @GetMapping
    public StatusResponse getStatus() {
        return new StatusResponse(applicationName, "UP");
    }
}
