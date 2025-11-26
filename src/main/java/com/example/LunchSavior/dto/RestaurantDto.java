package com.example.LunchSavior.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RestaurantDto(
                @NotBlank String name,
                @NotBlank String address,
                @NotNull Double latitude,
                @NotNull Double longitude,
                @NotNull Integer priceRange) {
}
