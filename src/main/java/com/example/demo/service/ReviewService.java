package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.ReviewCreateRequest;
import com.example.demo.entity.MenuItem;
import com.example.demo.entity.Restaurant;
import com.example.demo.entity.Review;
import com.example.demo.entity.User;
import com.example.demo.exception.BusinessValidationException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.filter.ReviewFilter;
import com.example.demo.repository.MenuItemRepository;
import com.example.demo.repository.RestaurantRepository;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.specification.ReviewSpecification;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;

    public ReviewService(
            ReviewRepository reviewRepository,
            UserRepository userRepository,
            RestaurantRepository restaurantRepository,
            MenuItemRepository menuItemRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
    }

    @Transactional(readOnly = true)
    public List<Review> getAll(ReviewFilter filter) {
        return reviewRepository.findAll(ReviewSpecification.fromFilter(filter));
    }

    @Transactional
    public Review create(ReviewCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + request.getUserId() + " not found"));

        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Restaurant with id " + request.getRestaurantId() + " not found"));

        MenuItem menuItem = null;
        if (request.getMenuItemId() != null) {
            menuItem = menuItemRepository.findById(request.getMenuItemId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Menu item with id " + request.getMenuItemId() + " not found"));

            if (!menuItem.getRestaurant().getId().equals(restaurant.getId())) {
                throw new BusinessValidationException("Menu item does not belong to selected restaurant");
            }
        }

        Review review = new Review();
        review.setUser(user);
        review.setRestaurant(restaurant);
        review.setMenuItem(menuItem);
        review.setRating(request.getRating());
        review.setComment(request.getComment() != null ? request.getComment().trim() : null);

        return reviewRepository.save(review);
    }
}