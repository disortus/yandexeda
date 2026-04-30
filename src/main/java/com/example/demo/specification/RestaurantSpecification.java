package com.example.demo.specification;

import com.example.demo.entity.Restaurant;
import com.example.demo.filter.RestaurantFilter;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.util.ArrayList;
import java.util.List;

// пример

public class RestaurantSpecification {

    public static Specification<Restaurant> fromFilter(RestaurantFilter f) {
        return (Root<Restaurant> root,
                CriteriaQuery<?> query,
                CriteriaBuilder cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // TODO: make getters and setter to filters

            if (f.getCity() != null) {
                predicates.add(cb.equal(root.get("city"), f.getCity()));
            }

            if (f.getMinRating() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("rating"), f.getMinRating()));
            }

            if (f.getTag() != null) {
                predicates.add(cb.isMember(f.getTag(), root.get("tags")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}