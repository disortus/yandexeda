package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.MenuItem;
import com.example.demo.filter.MenuItemFilter;
import com.example.demo.service.MenuItemService;

// пример

@RestController
@RequestMapping("/restaurants/{restaurantId}/dishes")
public class MenuItemController {

    private final MenuItemService service;

    public MenuItemController(MenuItemService service) {
        this.service = service;
    }

    @GetMapping
    public List<MenuItem> getAll(
            @PathVariable Long restaurantId,
            MenuItemFilter filter
    ) {
        return service.getAll(restaurantId, filter);
    }

    @GetMapping("/{dishId}")
    public MenuItem getById(@PathVariable Long dishId) {
        return service.getById(dishId);
    }
}