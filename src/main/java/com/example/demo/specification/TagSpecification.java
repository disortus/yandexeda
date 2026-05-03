package com.example.demo.specification;

import com.example.demo.entity.Tag;
import com.example.demo.filter.TagFilter;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.List;

public class TagSpecification {
    public static Specification<Tag> fromFilter(TagFilter f){
        return (Root<Tag> root,
                CriteriaQuery<?> query,
                CriteriaBuilder cb) -> {
                    List<Predicate> predicates = new ArrayList<>();

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