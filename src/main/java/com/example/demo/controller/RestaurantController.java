package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Restaurant;
import com.example.demo.filter.RestaurantFilter;
import com.example.demo.service.RestaurantService;

// пример

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {

    private final RestaurantService service;

    public RestaurantController(RestaurantService service) {
        this.service = service;
    }

    @GetMapping
    public List<Restaurant> getAll(RestaurantFilter filter) {
        return service.getAll(filter);
    }

    @GetMapping("/{id}")
    public Restaurant getById(@PathVariable Long id) {
        return service.getById(id);
    }
}