package com.example.demo.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.demo.entity.Category;
import com.example.demo.filter.CategoryFilter;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class CategorySpecification {
    public static Specification<Category> fromFilter(CategoryFilter f){
        return (Root<Category> root,
                CriteriaQuery<?> query,
                CriteriaBuilder cb) -> {
                    List<Predicate> predicates = new ArrayList<>();

                    if (f == null) {
                        return cb.and(predicates.toArray(new Predicate[0]));
                    }

                    if(f.getNameSearch() != null){
                        predicates.add(cb.like(
                            cb.lower(root.get("name")),
                            "%" + f.getNameSearch().toLowerCase() + "%"
                        ));
                    }
                    return cb.and(predicates.toArray(new Predicate[0]));
                };
    }
}