package com.example.demo.specification;

import com.example.demo.entity.Address;
import com.example.demo.filter.AddressFilter;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.List;

public class AddressSpecification {
    public static Specification<Address> fromFilter(AddressFilter f){
        return (Root<Address> root,
                CriteriaQuery<?> query,
                CriteriaBuilder cb) -> {
                    List<Predicate> predicates = new ArrayList<>();

                    if(f.getUserId() != null) {
                        predicates.add(cb.equal(root.get("userId"), f.getUserId()));
                    }

                    if(f.getAddressTextSearch() != null){
                        predicates.add(cb.like(
                            cb.lower(root.get("addressText")),
                            "%" + f.getAddressTextSearch().toLowerCase() + "%"
                        ));
                    }
                    return cb.and(predicates.toArray(new Predicate[0]));
                };
    }
}