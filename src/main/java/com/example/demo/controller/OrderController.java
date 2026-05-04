package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.OrderCreateRequest;
import com.example.demo.dto.OrderResponse;
import com.example.demo.entity.Order;
import com.example.demo.enums.OrderStatus;
import com.example.demo.filter.OrderFilter;
import com.example.demo.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "API для управления заказами")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "Получить список всех заказов")
    public ResponseEntity<List<OrderResponse>> getAll(OrderFilter filter) {
        List<Order> orders = orderService.getAll(filter);
        return ResponseEntity.ok(orders.stream().map(OrderResponse::fromEntity).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить заказ по ID")
    public ResponseEntity<OrderResponse> getById(@PathVariable Long id) {
        return orderService.getById(id)
                .map(order -> ResponseEntity.ok(OrderResponse.fromEntity(order)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Создать новый заказ")
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderCreateRequest request) {
        Order order = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.fromEntity(order));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Обновить статус заказа")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable Long id, @RequestBody OrderStatus status) {
        Order order = orderService.updateStatus(id, status);
        return ResponseEntity.ok(OrderResponse.fromEntity(order));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить заказ")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}