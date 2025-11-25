package com.example.LunchSavior.repository;

import com.example.LunchSavior.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    /**
     * Find restaurants within a certain distance using the Haversine formula.
     *
     * @param userLat    User's latitude
     * @param userLon    User's longitude
     * @param distanceKm Search radius in kilometers
     * @return List of restaurants within the distance
     */
    @Query(value = "SELECT * FROM restaurants r " +
            "WHERE (6371 * acos(cos(radians(:userLat)) * cos(radians(r.latitude)) * " +
            "cos(radians(r.longitude) - radians(:userLon)) + " +
            "sin(radians(:userLat)) * sin(radians(r.latitude)))) < :distanceKm", nativeQuery = true)
    List<Restaurant> findNearbyRestaurants(@Param("userLat") double userLat,
            @Param("userLon") double userLon,
            @Param("distanceKm") double distanceKm);
}
