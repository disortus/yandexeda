package com.example.demo.specification;

import com.example.demo.entity.Order;
import com.example.demo.filter.OrderFilter;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.List;

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

                    return cb.and(predicates.toArray(new Predicate[0]));
                };
    }
}