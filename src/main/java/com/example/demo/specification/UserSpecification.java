package com.example.demo.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.demo.entity.User;
import com.example.demo.filter.UserFilter;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class UserSpecification {
    public static Specification<User> fromFilter(UserFilter f){
        return (Root<User> root,
                CriteriaQuery<?> query,
                CriteriaBuilder cb) -> {
                    List<Predicate> predicates = new ArrayList<>();

                    if(f.getUserNameSearch() != null){
                        predicates.add(cb.like(
                            cb.lower(root.get("username")),
                            "%" + f.getUserNameSearch().toLowerCase() + "%"
                        ));
                    }
                    
                    if(f.getPhoneSearch() != null){
                        predicates.add(cb.like(
                            root.get("phone"),
                            "%" + f.getPhoneSearch() + "%"
                        ));
                    }

                    if(f.getRole() != null){
                        predicates.add(cb.equal(root.get("role"), f.getRole()));
                    }

                    return cb.and(predicates.toArray(new Predicate[0]));

                };
    }
}