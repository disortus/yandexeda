package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {
    List<Review> findByUserId(Long userId);

    List<Review> findByRestaurantId(Long restaurantId);

    List<Review> findByMenuItemId(Long menuItemId);

    boolean existsByUserIdAndMenuItemId(Long userId, Long menuItemId);
}
