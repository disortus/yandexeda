package com.example.demo.service;

import java.util.List;

import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.AddressCreateRequest;
import com.example.demo.entity.Address;
import com.example.demo.entity.Restaurant;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.AddressRepository;
import com.example.demo.repository.RestaurantRepository;
import com.example.demo.repository.UserRepository;

@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    public AddressService(
            AddressRepository addressRepository,
            UserRepository userRepository,
            RestaurantRepository restaurantRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @Transactional
    public Address createForUser(Long userId, AddressCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));

        Restaurant restaurant = null;
        if (request.getRestaurantId() != null) {
            restaurant = restaurantRepository.findById(request.getRestaurantId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Restaurant with id " + request.getRestaurantId() + " not found"));
        }

        Address address = new Address();
        address.setUser(user);
        address.setRestaurant(restaurant);
        address.setAddressText(request.getAddressText().trim());
        address.setLocation(new Point(request.getLng(), request.getLat()));

        return addressRepository.save(address);
    }

    @Transactional(readOnly = true)
    public List<Address> getMyAddresses(Long userId) {
        return addressRepository.findByUserId(userId);
    }
}
