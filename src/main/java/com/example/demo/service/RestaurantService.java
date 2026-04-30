package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Restaurant;
import com.example.demo.filter.RestaurantFilter;
import com.example.demo.repository.RestaurantRepository;
import com.example.demo.specification.RestaurantSpecification;

// пример

@Service
public class RestaurantService {

    private final RestaurantRepository repository;

    public RestaurantService(RestaurantRepository repository) {
        this.repository = repository;
    }

    public List<Restaurant> getAll(RestaurantFilter filter) {
        return repository.findAll(
                RestaurantSpecification.fromFilter(filter)
        );
    }

    public Restaurant getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }
}