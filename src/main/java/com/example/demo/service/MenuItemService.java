package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.entity.MenuItem;
import com.example.demo.filter.MenuItemFilter;
import com.example.demo.repository.MenuItemRepository;
import com.example.demo.specification.MenuItemSpecification;

// пример

@Service
public class MenuItemService {

    private final MenuItemRepository repository;

    public MenuItemService(MenuItemRepository repository) {
        this.repository = repository;
    }

    public List<MenuItem> getAll(Long restaurantId, MenuItemFilter filter) {
        return repository.findAll(
                MenuItemSpecification.fromFilter(restaurantId, filter)
        );
    }

    public MenuItem getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }
}