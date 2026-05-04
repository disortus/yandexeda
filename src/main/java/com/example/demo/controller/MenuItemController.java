package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.MenuItemCreateRequest;
import com.example.demo.dto.MenuItemResponse;
import com.example.demo.dto.MenuItemUpdateRequest;
import com.example.demo.dto.TagResponse;
import com.example.demo.filter.MenuItemFilter;
import com.example.demo.service.MenuItemService;

import jakarta.validation.Valid;

// пример

@RestController
@RequestMapping("/restaurants/{restaurantId}/dishes")
public class MenuItemController {

    private final MenuItemService service;

    public MenuItemController(MenuItemService service) {
        this.service = service;
    }

    @GetMapping
    public List<MenuItemResponse> getAll(
            @PathVariable Long restaurantId,
            MenuItemFilter filter
    ) {
        return service.getAll(restaurantId, filter).stream()
                .map(MenuItemResponse::fromEntity)
                .toList();
    }

    @GetMapping("/{dishId}")
    public MenuItemResponse getById(@PathVariable Long dishId) {
        return MenuItemResponse.fromEntity(service.getById(dishId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<MenuItemResponse> create(
            @PathVariable Long restaurantId,
            @Valid @RequestBody MenuItemCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MenuItemResponse.fromEntity(service.create(restaurantId, request)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{dishId}")
    public MenuItemResponse update(
            @PathVariable Long dishId,
            @Valid @RequestBody MenuItemUpdateRequest request) {
        return MenuItemResponse.fromEntity(service.update(dishId, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{dishId}")
    public ResponseEntity<Void> delete(@PathVariable Long dishId) {
        service.delete(dishId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{dishId}/tags")
    public List<TagResponse> getTags(@PathVariable Long dishId) {
        return service.getTags(dishId).stream()
                .map(tag -> new TagResponse(tag.getId(), tag.getName()))
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{dishId}/tags/{tagId}")
    public ResponseEntity<Void> addTag(
            @PathVariable Long dishId,
            @PathVariable Long tagId) {
        service.addTag(dishId, tagId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{dishId}/tags/{tagId}")
    public ResponseEntity<Void> removeTag(
            @PathVariable Long dishId,
            @PathVariable Long tagId) {
        service.removeTag(dishId, tagId);
        return ResponseEntity.noContent().build();
    }
}