package com.example.demo.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.RestaurantRecommendationResponse;
import com.example.demo.repository.RestaurantRecommendationProjection;
import com.example.demo.repository.RestaurantRepository;

@Service
public class RestaurantRecommendationService {
    private static final BigDecimal DEFAULT_MIN_RATING = new BigDecimal("4.00");
    private static final int DEFAULT_LIMIT = 10;

    private final RestaurantRepository restaurantRepository;

    public RestaurantRecommendationService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Transactional(readOnly = true)
    public List<RestaurantRecommendationResponse> getRecommendationsForUser(Long customerId) {
        List<RestaurantRecommendationProjection> projections = restaurantRepository.findRecommendedRestaurants(
                customerId,
                DEFAULT_MIN_RATING,
                DEFAULT_LIMIT);

        return projections.stream()
                .map(p -> new RestaurantRecommendationResponse(
                        p.getRestaurantId(),
                        p.getRestaurantName(),
                        p.getRestaurantRating() != null
                                ? p.getRestaurantRating().setScale(2, RoundingMode.HALF_UP)
                                : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                        p.getRelevanceScore()))
                .toList();
    }
}