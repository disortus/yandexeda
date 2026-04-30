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

// пример

public class MenuItemSpecification {
     public static Specification<MenuItem> fromFilter(
            Long restaurantId,
            MenuItemFilter f
    ) {                 
        return (Root<MenuItem> root,
                CriteriaQuery<?> query,
                CriteriaBuilder cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // обязательно фильтр по ресторану
            predicates.add(cb.equal(
                    root.get("restaurant").get("id"),
                    restaurantId
            ));

            // TODO: make getters and setters to MenuItemFilter                 

            if (f.getCategory() != null) {
                predicates.add(cb.equal(
                        root.get("category"),
                        f.getCategory()
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

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
