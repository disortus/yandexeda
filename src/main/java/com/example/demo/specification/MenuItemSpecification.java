package com.example.demo.specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.demo.entity.MenuItem;
import com.example.demo.filter.MenuItemFilter;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class MenuItemSpecification {
     public static Specification<MenuItem> fromFilter(
            Long restaurantId,
            MenuItemFilter filter
    ) {                 
        return (Root<MenuItem> root,
                CriteriaQuery<?> query,
                CriteriaBuilder cb) -> {

            List<Predicate> predicates = new ArrayList<>();
            MenuItemFilter f = filter != null ? filter : new MenuItemFilter();

            if (f.getCategoryId() != null) {
                predicates.add(cb.equal(
                        root.get("category").get("id"),
                        f.getCategoryId()
                ));
            }

            if (f.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("price"),
                        BigDecimal.valueOf(f.getMinPrice())
                ));
            }

            if (f.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("price"),
                        BigDecimal.valueOf(f.getMaxPrice())
                ));
            }

            if (restaurantId != null){
                predicates.add(cb.equal(root.get("restaurant").get("id"), restaurantId));
            }

            if (f.getRestaurantId() != null){
                predicates.add(cb.equal(root.get("restaurant").get("id"), f.getRestaurantId()));
            }

            if (f.getIsAvailable() != null){
                predicates.add(cb.equal(root.get("isAvailable"), f.getIsAvailable()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
