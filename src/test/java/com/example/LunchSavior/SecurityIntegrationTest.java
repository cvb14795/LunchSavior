package com.example.LunchSavior;

import com.example.LunchSavior.controller.AuthenticationController;
import com.example.LunchSavior.dto.RestaurantDto;
import com.example.LunchSavior.entity.Role;
import com.example.LunchSavior.entity.User;
import com.example.LunchSavior.repository.UserRepository;
import com.example.LunchSavior.service.RestaurantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "application.security.jwt.secret-key=c2VjcmV0a2V5c2VjcmV0a2V5c2VjcmV0a2V5c2VjcmV0a2V5c2VjcmV0a2V5"
})
@AutoConfigureMockMvc
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RestaurantService restaurantService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testPublicEndpoint_Gacha() throws Exception {
        // 1. 公開端點測試：GET /api/v1/restaurants/gacha
        // Mock service response
        when(restaurantService.gacha(any(Double.class), any(Double.class)))
                .thenReturn(new RestaurantDto("Test Restaurant", "Test Address", 25.0, 121.0, 1));

        mockMvc.perform(get("/api/v1/restaurants/gacha")
                .param("lat", "25.0")
                .param("lon", "121.0"))
                .andExpect(status().isOk());
    }

    @Test
    void testProtectedEndpoint_NoToken() throws Exception {
        // 2. 受保護端點測試 (無 Token)：POST /api/v1/restaurants
        RestaurantDto dto = new RestaurantDto("New Restaurant", "Address", 25.0, 121.0, 2);

        mockMvc.perform(post("/api/v1/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testRegistration() throws Exception {
        // 3. 註冊流程測試：POST /api/v1/auth/register
        AuthenticationController.RegisterRequest request = new AuthenticationController.RegisterRequest();
        request.setEmail("newuser@example.com");
        request.setPassword("password");

        // Mock repository save
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void testProtectedEndpoint_WithToken() throws Exception {
        // 4. 受保護端點測試 (有 Token)
        String email = "test@example.com";
        String password = "password";

        // Step 1: Register to get token
        AuthenticationController.RegisterRequest registerRequest = new AuthenticationController.RegisterRequest();
        registerRequest.setEmail(email);
        registerRequest.setPassword(password);

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String responseContent = mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        AuthenticationController.AuthenticationResponse authResponse = objectMapper.readValue(responseContent,
                AuthenticationController.AuthenticationResponse.class);
        String token = authResponse.getToken();

        // Step 2: Use token to access protected endpoint
        // Mock findByEmail for JwtAuthenticationFilter -> UserDetailsService
        User mockUser = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(Role.USER)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        RestaurantDto dto = new RestaurantDto("Protected Restaurant", "Address", 25.0, 121.0, 2);

        mockMvc.perform(post("/api/v1/restaurants")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }
}
