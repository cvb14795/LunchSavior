package com.example.LunchSavior.service;

import com.example.LunchSavior.dto.RestaurantDto;
import com.example.LunchSavior.entity.Restaurant;
import com.example.LunchSavior.exception.RestaurantNotFoundException;
import com.example.LunchSavior.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final Random random = new Random();

    /**
     * Add a new restaurant.
     *
     * @param dto Data transfer object containing restaurant details
     */
    public void addRestaurant(RestaurantDto dto) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(dto.name());
        restaurant.setAddress(dto.address());
        restaurant.setLatitude(dto.latitude());
        restaurant.setLongitude(dto.longitude());
        restaurant.setPriceRange(dto.priceRange());
        restaurantRepository.save(restaurant);
    }

    /**
     * Gacha: Find a random restaurant within the specified distance.
     *
     * @param lat        User's latitude
     * @param lon        User's longitude
     * @param distanceKm Search radius in kilometers
     * @return The selected RestaurantDto
     * @throws RestaurantNotFoundException if no restaurants found within the
     *                                     specified distance
     */
    public RestaurantDto gacha(double lat, double lon, BigDecimal distanceKm) {
        // Find restaurants within the specified distance
        List<Restaurant> nearbyRestaurants = restaurantRepository.findNearbyRestaurants(lat, lon,
                distanceKm.doubleValue());

        if (nearbyRestaurants.isEmpty()) {
            throw new RestaurantNotFoundException("No restaurants found within " + distanceKm + "km!");
        }

        // Pick one randomly
        Restaurant selected = nearbyRestaurants.get(random.nextInt(nearbyRestaurants.size()));

        // Convert to DTO
        return new RestaurantDto(
                selected.getName(),
                selected.getAddress(),
                selected.getLatitude(),
                selected.getLongitude(),
                selected.getPriceRange());
    }
}
