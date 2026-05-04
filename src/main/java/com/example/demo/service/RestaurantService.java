package com.example.demo.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.RestaurantCreateRequest;
import com.example.demo.dto.RestaurantRecommendationResponse;
import com.example.demo.dto.RestaurantResponse;
import com.example.demo.dto.RestaurantUpdateRequest;
import com.example.demo.entity.Restaurant;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.filter.RestaurantFilter;
import com.example.demo.repository.RestaurantRepository;
import com.example.demo.specification.RestaurantSpecification;

// пример

@Service
public class RestaurantService {

    private final RestaurantRepository repository;
    private final RestaurantRecommendationService recommendationService;

    public RestaurantService(RestaurantRepository repository, RestaurantRecommendationService recommendationService) {
        this.repository = repository;
        this.recommendationService = recommendationService;
    }

    @Transactional(readOnly = true)
    public List<RestaurantResponse> getAll(RestaurantFilter filter) {
        List<Restaurant> restaurants = repository.findAll(
                RestaurantSpecification.fromFilter(filter)
        );

        return restaurants.stream()
                .map(r -> RestaurantResponse.fromEntity(r, getRestaurantRating(r.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public RestaurantResponse getById(Long id) {
        Restaurant restaurant = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant with id " + id + " not found"));
        return RestaurantResponse.fromEntity(restaurant, getRestaurantRating(id));
    }

    @Transactional
    public RestaurantResponse create(RestaurantCreateRequest request) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(request.getName().trim());
        restaurant.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);

        Restaurant saved = repository.save(restaurant);
        return RestaurantResponse.fromEntity(saved, BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
    }

    @Transactional
    public RestaurantResponse update(Long id, RestaurantUpdateRequest request) {
        Restaurant restaurant = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant with id " + id + " not found"));

        if (request.getName() != null) {
            restaurant.setName(request.getName().trim());
        }
        if (request.getDescription() != null) {
            restaurant.setDescription(request.getDescription().trim());
        }

        Restaurant saved = repository.save(restaurant);
        return RestaurantResponse.fromEntity(saved, getRestaurantRating(saved.getId()));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Restaurant with id " + id + " not found");
        }
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<RestaurantRecommendationResponse> getRecommendations(Long customerId) {
        return recommendationService.getRecommendationsForUser(customerId);
    }

    private BigDecimal getRestaurantRating(Long restaurantId) {
        BigDecimal rating = repository.calculateAverageRatingByRestaurantId(restaurantId);
        if (rating == null) {
            rating = BigDecimal.ZERO;
        }
        return rating.setScale(2, RoundingMode.HALF_UP);
    }
}