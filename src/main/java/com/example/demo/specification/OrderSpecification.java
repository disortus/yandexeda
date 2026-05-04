package com.example.demo.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.demo.entity.Order;
import com.example.demo.filter.OrderFilter;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class OrderSpecification {
    public static Specification<Order> fromFilter(OrderFilter f){
        return (Root<Order> root,
                CriteriaQuery<?> query,
                CriteriaBuilder cb) -> {
                    
                    List<Predicate> predicates = new ArrayList<>();

                    if (f.getRestaurantId() != null){
                        predicates.add(cb.equal(root.get("restaurantId"), f.getRestaurantId()));
                    }

                    if (f.getStatus() != null){
                        predicates.add(cb.equal(root.get("status"), f.getStatus()));
                    }

                    if (f.getMinTotalPrice() != null) {
                        predicates.add(cb.greaterThanOrEqualTo(
                                root.get("price"),
                                f.getMinTotalPrice()
                        ));
                    }

                    if (f.getMaxTotalPrice() != null) {
                        predicates.add(cb.lessThanOrEqualTo(
                                root.get("price"),
                                f.getMaxTotalPrice()
                        ));
                    }

                    return cb.and(predicates.toArray(new Predicate[0]));
                };
    }
}