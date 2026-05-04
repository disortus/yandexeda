package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.AddressCreateRequest;
import com.example.demo.dto.AddressResponse;
import com.example.demo.security.JwtService;
import com.example.demo.service.AddressService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/addresses")
public class AddressController {
    private final AddressService addressService;
    private final JwtService jwtService;

    public AddressController(AddressService addressService, JwtService jwtService) {
        this.addressService = addressService;
        this.jwtService = jwtService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<AddressResponse> create(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody AddressCreateRequest request) {
        Long userId = extractUserId(authorization);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AddressResponse.fromEntity(addressService.createForUser(userId, request)));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public List<AddressResponse> getMyAddresses(@RequestHeader("Authorization") String authorization) {
        Long userId = extractUserId(authorization);
        return addressService.getMyAddresses(userId).stream().map(AddressResponse::fromEntity).toList();
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
