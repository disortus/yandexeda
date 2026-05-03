package com.example.demo.specification;

import com.example.demo.entity.Review;
import com.example.demo.filter.ReviewFilter;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.List;

public class ReviewSpecification {
    public static Specification<Review> fromFilter(ReviewFilter f){
        return (Root<Review> root,
                CriteriaQuery<?> query,
                CriteriaBuilder cb) -> {
                    List<Predicate> predicates = new ArrayList<>();

                    if(f.getUserId() != null) {
                        predicates.add(cb.equal(root.get("userId"), f.getUserId()));
                    }

                    if (f.getRestaurantId() != null){
                        predicates.add(cb.equal(root.get("restaurantId"), f.getRestaurantId()));
                    }

                    if (f.getMenuItemId() != null){
                        predicates.add(cb.equal(root.get("menuItemId"), f.getMenuItemId()));
                    }

                    if (f.getMinRating() != null) {
                        predicates.add(cb.greaterThanOrEqualTo(
                                root.get("rating"),
                                f.getMinRating()
                        ));
                    }

                    if (f.getMaxRating() != null) {
                        predicates.add(cb.lessThanOrEqualTo(
                                root.get("rating"),
                                f.getMaxRating()
                        ));
                    }

                    return cb.and(predicates.toArray(new Predicate[0]));
                };
    }
}