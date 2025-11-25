package com.example.LunchSavior.dto;

public record RestaurantDto(
        String name,
        String address,
        Double latitude,
        Double longitude,
        Integer priceRange) {
}
