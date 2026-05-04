package com.example.demo.service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.data.geo.Point;
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
import com.example.demo.repository.AddressGeoProjection;
import com.example.demo.repository.AddressRepository;
import com.example.demo.repository.MenuItemRepository;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.RestaurantRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.specification.OrderSpecification;

@Service
public class OrderService {

    private static final List<OrderStatus> ACTIVE_COURIER_STATUSES = List.of(
            OrderStatus.ASSIGNED,
            OrderStatus.DELIVERING);

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
        if (request.items() == null || request.items().isEmpty()) {
            throw new BusinessValidationException("Order must contain at least one item");
        }

        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with id " + customerId + " not found"));
        
        Restaurant restaurant = restaurantRepository.findById(request.restaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant with id " + request.restaurantId() + " not found"));
        
        Address deliveryAddress = resolveDeliveryAddress(customer, request);

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

    private Address resolveDeliveryAddress(User customer, OrderCreateRequest request) {
        if (request.deliveryAddressId() != null) {
            Address deliveryAddress = addressRepository.findById(request.deliveryAddressId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Delivery address with id " + request.deliveryAddressId() + " not found"));

            if (deliveryAddress.getUser() == null || !deliveryAddress.getUser().getId().equals(customer.getId())) {
                throw new BusinessValidationException("Delivery address does not belong to current user");
            }
            return deliveryAddress;
        }

        if (request.deliveryAddress() != null) {
            Address address = new Address();
            address.setUser(customer);
            address.setAddressText(request.deliveryAddress().getAddressText().trim());
            address.setLocation(new Point(request.deliveryAddress().getLng(), request.deliveryAddress().getLat()));
            return addressRepository.save(address);
        }

        throw new BusinessValidationException("Either deliveryAddressId or deliveryAddress must be provided");
    }

    private void assignNearestCourier(Order order) {
        var restaurantCoordinatesOpt = addressRepository.findRestaurantAddressWithLocation(order.getRestaurant().getId())
                .flatMap(a -> addressRepository.findCoordinatesByAddressId(a.getId()));

        var customerCoordinatesOpt = addressRepository.findCoordinatesByAddressId(order.getDeliveryAddress().getId());

        if (restaurantCoordinatesOpt.isEmpty() || customerCoordinatesOpt.isEmpty()) {
            return;
        }

        AddressGeoProjection restaurantCoordinates = restaurantCoordinatesOpt.get();

        // Find available couriers
        List<User> couriers = userRepository.findByRole(UserRole.COURIER);

        User nearestCourier = couriers.stream()
                .filter(courier -> orderRepository.countByCourierIdAndStatusIn(
                        courier.getId(),
                        ACTIVE_COURIER_STATUSES) == 0)
                .map(courier -> new CourierCandidate(
                        courier,
                        calculateCourierRestaurantDistance(courier.getId(), restaurantCoordinates)))
                .filter(candidate -> candidate.distanceKm() != null)
                .min(Comparator.comparing(CourierCandidate::distanceKm))
                .map(CourierCandidate::courier)
                .orElse(null);

        if (nearestCourier != null) {
            order.setCourier(nearestCourier);
            order.setStatus(OrderStatus.ASSIGNED);
        }
    }

    private Double calculateCourierRestaurantDistance(Long courierId, AddressGeoProjection restaurantCoordinates) {
        return addressRepository.findByUserId(courierId).stream()
                .map(Address::getId)
                .map(addressRepository::findCoordinatesByAddressId)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(courierCoordinates -> distanceInKm(
                        courierCoordinates.getLat(),
                        courierCoordinates.getLng(),
                        restaurantCoordinates.getLat(),
                        restaurantCoordinates.getLng()))
                .min(Double::compareTo)
                .orElse(null);
    }

    private double distanceInKm(double lat1, double lon1, double lat2, double lon2) {
        final double earthRadiusKm = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadiusKm * c;
    }

    private record CourierCandidate(User courier, Double distanceKm) {
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