package com.example.demo.specification;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.entity.MenuItem;
import com.example.demo.filter.MenuItemFilter;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class MenuItemSpecification {
     public static Specification<MenuItem> fromFilter(
            Long restaurantId,
            MenuItemFilter f
    ) {                 
        return (Root<MenuItem> root,
                CriteriaQuery<?> query,
                CriteriaBuilder cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (f.getCategoryId() != null) {
                predicates.add(cb.equal(
                        root.get("categoryId"),
                        f.getCategoryId()
                ));
            }

            if (f.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("price"),
                        f.getMinPrice()
                ));
            }

            if (f.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("price"),
                        f.getMaxPrice()
                ));
            }

            if (f.getRestaurantId() != null){
                predicates.add(cb.equal(root.get("restaurantId"), f.getRestaurantId()));
            }

            if (f.getIsAvailable() != null){
                predicates.add(cb.equal(root.get("isAvailable"), f.getIsAvailable()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
