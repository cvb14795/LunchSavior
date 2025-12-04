package com.example.LunchSavior.controller;

import com.example.LunchSavior.dto.RestaurantDto;
import com.example.LunchSavior.service.RestaurantService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
@Tag(name = "Restaurant", description = "餐廳管理 API")
public class RestaurantController {

    private final RestaurantService restaurantService;

    /**
     * Create a new restaurant.
     *
     * @param dto Restaurant details
     * @return HTTP 201 Created
     */
    @Operation(summary = "新增餐廳", description = "建立一間新的餐廳資料")
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
    @Operation(summary = "餐廳抽籤", description = "根據經緯度隨機抽取一間附近的餐廳")
    @GetMapping("/gacha")
    public ResponseEntity<RestaurantDto> gacha(@RequestParam double lat, @RequestParam double lon) {
        BigDecimal distanceKm = new BigDecimal("1.0");
        RestaurantDto result = restaurantService.gacha(lat, lon, distanceKm);
        return ResponseEntity.ok(result);
    }
}
