package com.example.demo.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.OrderCreateRequest;
import com.example.demo.entity.Address;
import com.example.demo.entity.MenuItem;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.Restaurant;
import com.example.demo.entity.User;
import com.example.demo.enums.OrderStatus;
import com.example.demo.enums.UserRole;
import com.example.demo.exception.BusinessValidationException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.filter.OrderFilter;
import com.example.demo.repository.AddressRepository;
import com.example.demo.repository.MenuItemRepository;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.RestaurantRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.specification.OrderSpecification;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final AddressRepository addressRepository;
    private final MenuItemRepository menuItemRepository;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        UserRepository userRepository,
                        RestaurantRepository restaurantRepository,
                        AddressRepository addressRepository,
                        MenuItemRepository menuItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.addressRepository = addressRepository;
        this.menuItemRepository = menuItemRepository;
    }

    public List<Order> getAll(OrderFilter filter) {
        return orderRepository.findAll(OrderSpecification.fromFilter(filter));
    }

    public Optional<Order> getById(Long id) {
        return orderRepository.findById(id);
    }

    @Transactional
    public Order createOrder(Long customerId, OrderCreateRequest request) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with id " + customerId + " not found"));
        
        Restaurant restaurant = restaurantRepository.findById(request.restaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant with id " + request.restaurantId() + " not found"));
        
        Address deliveryAddress = addressRepository.findById(request.deliveryAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Delivery address with id " + request.deliveryAddressId() + " not found"));

        if (deliveryAddress.getUser() == null || !deliveryAddress.getUser().getId().equals(customer.getId())) {
            throw new BusinessValidationException("Delivery address does not belong to current user");
        }

        // Create order items
        List<OrderItem> orderItems = request.items().stream()
                .map(itemRequest -> {
                    MenuItem menuItem = menuItemRepository.findById(itemRequest.getMenuItemId())
                            .orElseThrow(() -> new ResourceNotFoundException("Menu item with id " + itemRequest.getMenuItemId() + " not found"));

                    if (!menuItem.getRestaurant().getId().equals(restaurant.getId())) {
                        throw new BusinessValidationException(
                                "Menu item with id " + menuItem.getId() + " does not belong to restaurant " + restaurant.getId());
                    }

                    return new OrderItem(null, menuItem, itemRequest.getQuantity(), menuItem.getPrice());
                })
                .toList();

        // Calculate total price
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderItem item : orderItems) {
            totalPrice = totalPrice.add(
                    item.getPrice().multiply(new BigDecimal(item.getQuantity()))
            );
        }

        // Create order
        Order order = new Order(customer, null, restaurant, deliveryAddress, OrderStatus.PENDING, totalPrice);
        order = orderRepository.save(order);

        // Save order items
        for (OrderItem item : orderItems) {
            item.setOrder(order);
            orderItemRepository.save(item);
        }

        // Assign nearest courier
        assignNearestCourier(order);

        return orderRepository.save(order);
    }

    private void assignNearestCourier(Order order) {
        // Find available couriers (simplified logic - in real app would use geolocation)
        List<User> couriers = userRepository.findByRole(UserRole.COURIER);

        if (!couriers.isEmpty()) {
            // Simple assignment: pick first available courier
            // In production, would calculate distance between courier location and restaurant
            User nearestCourier = couriers.get(0);
            order.setCourier(nearestCourier);
            order.setStatus(OrderStatus.ASSIGNED);
        }
    }

    @Transactional
    public Order updateStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id " + orderId + " not found"));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}