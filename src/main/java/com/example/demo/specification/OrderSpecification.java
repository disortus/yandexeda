package com.example.demo.specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;

import com.example.demo.entity.Order;
import com.example.demo.enums.OrderStatus;
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

                    if (f == null) {
                        return cb.and(predicates.toArray(new Predicate[0]));
                    }

                    if (f.getRestaurantId() != null){
                        predicates.add(cb.equal(root.get("restaurant").get("id"), f.getRestaurantId()));
                    }

                    if (f.getStatus() != null){
                        try {
                            OrderStatus status = OrderStatus.valueOf(f.getStatus().trim().toUpperCase(Locale.ROOT));
                            predicates.add(cb.equal(root.get("status"), status));
                        } catch (IllegalArgumentException ignored) {
                            predicates.add(cb.disjunction());
                        }
                    }

                    if (f.getMinTotalPrice() != null) {
                        predicates.add(cb.greaterThanOrEqualTo(
                                root.get("totalPrice"),
                                BigDecimal.valueOf(f.getMinTotalPrice())
                        ));
                    }

                    if (f.getMaxTotalPrice() != null) {
                        predicates.add(cb.lessThanOrEqualTo(
                                root.get("totalPrice"),
                                BigDecimal.valueOf(f.getMaxTotalPrice())
                        ));
                    }

                    return cb.and(predicates.toArray(new Predicate[0]));
                };
    }
}