package com.example.LunchSavior.controller;

import com.example.LunchSavior.dto.RestaurantDto;
import com.example.LunchSavior.exception.RestaurantNotFoundException;
import com.example.LunchSavior.service.RestaurantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.example.LunchSavior.config.JwtAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

@WebMvcTest(RestaurantController.class)
@AutoConfigureMockMvc(addFilters = false)
public class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RestaurantService restaurantService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void addRestaurant_Success() throws Exception {
        RestaurantDto dto = new RestaurantDto("Tasty Burger", "123 Main St", 25.0330, 121.5654, 2);

        doNothing().when(restaurantService).addRestaurant(any(RestaurantDto.class));

        mockMvc.perform(post("/api/v1/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    public void addRestaurant_Fail() throws Exception {
        RestaurantDto dto = new RestaurantDto("", "", null, null, null); // Invalid data

        mockMvc.perform(post("/api/v1/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void gacha_Success() throws Exception {
        RestaurantDto mockRestaurant = new RestaurantDto("Random Sushi", "456 Fish Rd", 25.04, 121.57, 3);

        when(restaurantService.gacha(anyDouble(), anyDouble(), any(BigDecimal.class))).thenReturn(mockRestaurant);

        mockMvc.perform(get("/api/v1/restaurants/gacha")
                .param("lat", "25.0330")
                .param("lon", "121.5654"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Random Sushi"))
                .andExpect(jsonPath("$.address").value("456 Fish Rd"));
    }

    @Test
    public void gacha_NotFound() throws Exception {
        when(restaurantService.gacha(anyDouble(), anyDouble(), any(BigDecimal.class)))
                .thenThrow(new RestaurantNotFoundException("No restaurants found within 1km!"));

        mockMvc.perform(get("/api/v1/restaurants/gacha")
                .param("lat", "25.0330")
                .param("lon", "121.5654"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No restaurants found within 1km!"));
    }
}
