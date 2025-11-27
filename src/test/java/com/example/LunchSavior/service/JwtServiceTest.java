package com.example.LunchSavior.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    // A valid 256-bit secret key encoded in Base64
    // "secretKey1234567890123456789012345678901234567890" (48 bytes) -> Base64
    private static final String SECRET_KEY = "c2VjcmV0S2V5MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MA==";

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        // Inject the secret key into the service
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET_KEY);

        // Setup a mock UserDetails
        userDetails = new User("testuser", "password", Collections.emptyList());
    }

    @Test
    void generateToken_shouldReturnToken() {
        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_shouldReturnCorrectUsername() {
        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUsername(token);
        assertEquals(userDetails.getUsername(), username);
    }

    @Test
    void isTokenValid_shouldReturnTrueForValidToken() {
        String token = jwtService.generateToken(userDetails);
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValid_shouldReturnFalseForInvalidUsername() {
        String token = jwtService.generateToken(userDetails);
        UserDetails otherUser = new User("otheruser", "password", Collections.emptyList());
        assertFalse(jwtService.isTokenValid(token, otherUser));
    }

    @Test
    void isTokenValid_shouldThrowExceptionForExpiredToken() {
        // Manually create an expired token
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        String expiredToken = Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24)) // Issued yesterday
                .expiration(new Date(System.currentTimeMillis() - 1000 * 60 * 60)) // Expired 1 hour ago
                .signWith(key)
                .compact();

        // Expect ExpiredJwtException because JwtService parses the token which
        // validates expiration
        assertThrows(ExpiredJwtException.class, () -> {
            jwtService.isTokenValid(expiredToken, userDetails);
        });
    }
}
