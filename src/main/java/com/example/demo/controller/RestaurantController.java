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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.RestaurantCreateRequest;
import com.example.demo.dto.RestaurantRecommendationResponse;
import com.example.demo.dto.RestaurantResponse;
import com.example.demo.dto.RestaurantUpdateRequest;
import com.example.demo.filter.RestaurantFilter;
import com.example.demo.security.JwtService;
import com.example.demo.service.RestaurantService;

import jakarta.validation.Valid;

// пример

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {

    private final RestaurantService service;
    private final JwtService jwtService;

    public RestaurantController(RestaurantService service, JwtService jwtService) {
        this.service = service;
        this.jwtService = jwtService;
    }

    @GetMapping
    public List<RestaurantResponse> getAll(RestaurantFilter filter) {
        return service.getAll(filter);
    }

    @GetMapping("/{id}")
    public RestaurantResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<RestaurantResponse> create(@Valid @RequestBody RestaurantCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public RestaurantResponse update(@PathVariable Long id, @Valid @RequestBody RestaurantUpdateRequest request) {
        return service.update(id, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/recommendations")
    public List<RestaurantRecommendationResponse> getRecommendations(
            @RequestHeader("Authorization") String authorization) {
        Long userId = extractUserId(authorization);
        return service.getRecommendations(userId);
    }

    private Long extractUserId(String authorization) {
        String token = authorization.substring(7).trim();
        Long userId = jwtService.extractUserId(token);
        if (userId == null) {
            throw new IllegalStateException("JWT does not contain userId claim");
        }
        return userId;
    }
}