package com.example.demo.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.demo.entity.Restaurant;
import com.example.demo.filter.RestaurantFilter;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class RestaurantSpecification {

    public static Specification<Restaurant> fromFilter(RestaurantFilter f) {
        return (Root<Restaurant> root,
                CriteriaQuery<?> query,
                CriteriaBuilder cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (f.getNameSearch() != null){
                predicates.add(cb.like(
                    cb.lower(root.get("name")),
                    "%" + f.getNameSearch().toLowerCase() + "%"
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}