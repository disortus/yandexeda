package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserId(Long userId);
    List<Address> findByRestaurantId(Long restaurantId);

    @Query(value = """
            SELECT
                ST_Y(a.location::geometry) AS lat,
                ST_X(a.location::geometry) AS lng
            FROM addresses a
            WHERE a.id = :addressId
            """, nativeQuery = true)
    Optional<AddressGeoProjection> findCoordinatesByAddressId(@Param("addressId") Long addressId);

    @Query(value = """
            SELECT
                a.id,
                a.user_id,
                a.restaurant_id,
                a.address_text,
                a.location
            FROM addresses a
            WHERE a.restaurant_id = :restaurantId
              AND a.location IS NOT NULL
            LIMIT 1
            """, nativeQuery = true)
    Optional<Address> findRestaurantAddressWithLocation(@Param("restaurantId") Long restaurantId);
}