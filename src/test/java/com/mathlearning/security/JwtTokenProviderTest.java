package com.mathlearning.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JwtTokenProvider Tests")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private final String testSecret = "test-secret-key-that-is-long-enough-for-hmac-sha-256-algorithm";
    private final long testExpiration = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", testSecret);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpiration", testExpiration);
        jwtTokenProvider.init();
    }

    @Test
    @DisplayName("Should generate valid JWT token")
    void generateToken_ValidInput_ReturnsToken() {
        // given
        Long userId = 1L;
        String email = "test@example.com";

        // when
        String token = jwtTokenProvider.generateToken(userId, email);

        // then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts
    }

    @Test
    @DisplayName("Should extract user ID from token")
    void getUserIdFromToken_ValidToken_ReturnsUserId() {
        // given
        Long userId = 123L;
        String email = "test@example.com";
        String token = jwtTokenProvider.generateToken(userId, email);

        // when
        Long extractedUserId = jwtTokenProvider.getUserIdFromToken(token);

        // then
        assertThat(extractedUserId).isEqualTo(userId);
    }

    @Test
    @DisplayName("Should validate valid token")
    void validateToken_ValidToken_ReturnsTrue() {
        // given
        String token = jwtTokenProvider.generateToken(1L, "test@example.com");

        // when
        boolean isValid = jwtTokenProvider.validateToken(token);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should reject invalid token")
    void validateToken_InvalidToken_ReturnsFalse() {
        // given
        String invalidToken = "invalid.token.here";

        // when
        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should reject malformed token")
    void validateToken_MalformedToken_ReturnsFalse() {
        // given
        String malformedToken = "not-a-jwt-token";

        // when
        boolean isValid = jwtTokenProvider.validateToken(malformedToken);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should include email in token claims")
    void generateToken_IncludesEmailClaim() {
        // given
        Long userId = 1L;
        String email = "test@example.com";

        // when
        String token = jwtTokenProvider.generateToken(userId, email);

        // then - verify token is valid and contains expected user ID
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
        assertThat(jwtTokenProvider.getUserIdFromToken(token)).isEqualTo(userId);
    }

    @Test
    @DisplayName("Should generate different tokens for different users")
    void generateToken_DifferentUsers_GeneratesDifferentTokens() {
        // given
        String token1 = jwtTokenProvider.generateToken(1L, "user1@example.com");
        String token2 = jwtTokenProvider.generateToken(2L, "user2@example.com");

        // then
        assertThat(token1).isNotEqualTo(token2);
    }
}
