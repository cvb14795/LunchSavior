package com.example.LunchSavior.controller;

import com.example.LunchSavior.dto.RestaurantDto;
import com.example.LunchSavior.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    /**
     * Create a new restaurant.
     *
     * @param dto Restaurant details
     * @return HTTP 201 Created
     */
    @PostMapping
    public ResponseEntity<Void> addRestaurant(@RequestBody @jakarta.validation.Valid RestaurantDto dto) {
        restaurantService.addRestaurant(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Gacha: Get a random restaurant nearby.
     *
     * @param lat User's latitude
     * @param lon User's longitude
     * @return The selected restaurant
     */
    @GetMapping("/gacha")
    public ResponseEntity<RestaurantDto> gacha(@RequestParam double lat, @RequestParam double lon) {
        RestaurantDto result = restaurantService.gacha(lat, lon);
        return ResponseEntity.ok(result);
    }
}
