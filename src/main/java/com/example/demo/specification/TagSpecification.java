package com.example.demo.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.demo.entity.Tag;
import com.example.demo.filter.TagFilter;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class TagSpecification {
    public static Specification<Tag> fromFilter(TagFilter f){
        return (Root<Tag> root,
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