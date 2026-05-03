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

public class RestaurantSpecification {

    public static Specification<Restaurant> fromFilter(RestaurantFilter f) {
        return (Root<Restaurant> root,
                CriteriaQuery<?> query,
                CriteriaBuilder cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (f.getName != null){
                predicates.add(cb.like(
                    cb.lower(root.get("name")),
                    "%" + f.getName().toLowerCase() + "%"
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}