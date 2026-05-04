package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Order;

@Repository
public interface OrderQueryRepository extends JpaRepository<Order, Long> {

    @Query(value = """
            SELECT DISTINCT o.courier_id
            FROM orders o
            WHERE o.status IN ('ASSIGNED', 'DELIVERING')
              AND o.courier_id IS NOT NULL
              AND o.courier_id = :courierId
            LIMIT 1
            """, nativeQuery = true)
    Optional<Long> findBusyCourierId(@Param("courierId") Long courierId);
}