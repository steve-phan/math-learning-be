package com.mathlearning.security;

public interface IJwtTokenProvider {
    String generateToken(Long userId, String email);

    Long getUserIdFromToken(String token);

    boolean validateToken(String authToken);
}
