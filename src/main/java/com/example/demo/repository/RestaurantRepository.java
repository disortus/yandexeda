package com.example.demo.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Restaurant;

// пример
// JpaSpecificationExecutor - ключ к фильтрам (аналог Mongo filter)

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long>, JpaSpecificationExecutor<Restaurant> {

    @Query("""
            select coalesce(avg(r.rating), 0)
            from Review r
            where r.restaurant.id = :restaurantId
            """)
    BigDecimal calculateAverageRatingByRestaurantId(@Param("restaurantId") Long restaurantId);

    @Query(value = """
            SELECT
                r.id AS restaurantId,
                r.name AS restaurantName,
                COALESCE(AVG(rv.rating), 0) AS restaurantRating,
                COUNT(*) AS relevanceScore
            FROM orders o
            JOIN order_items oi ON oi.order_id = o.id
            JOIN menu_items mi ON mi.id = oi.menu_item_id
            JOIN menu_item_tags mit ON mit.menu_item_id = mi.id
            JOIN menu_items similar_mi ON similar_mi.restaurant_id <> o.restaurant_id
            JOIN menu_item_tags similar_mit ON similar_mit.menu_item_id = similar_mi.id
                 AND similar_mit.tag_id = mit.tag_id
            JOIN restaurants r ON r.id = similar_mi.restaurant_id
            LEFT JOIN reviews rv ON rv.restaurant_id = r.id
            WHERE o.customer_id = :customerId
            GROUP BY r.id, r.name
            HAVING COALESCE(AVG(rv.rating), 0) >= :minRating
            ORDER BY relevanceScore DESC, restaurantRating DESC
            LIMIT :limitValue
            """, nativeQuery = true)
    List<RestaurantRecommendationProjection> findRecommendedRestaurants(
            @Param("customerId") Long customerId,
            @Param("minRating") BigDecimal minRating,
            @Param("limitValue") int limitValue);

}
