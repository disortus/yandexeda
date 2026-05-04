package com.example.demo.repository;

import java.math.BigDecimal;

public interface RestaurantRecommendationProjection {
    Long getRestaurantId();

    String getRestaurantName();

    BigDecimal getRestaurantRating();

    Long getRelevanceScore();
}