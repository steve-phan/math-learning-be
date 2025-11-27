package com.mathlearning.service;

import com.mathlearning.dto.AuthResponse;
import com.mathlearning.dto.LoginRequest;
import com.mathlearning.dto.RegisterRequest;

/**
 * Service interface for authentication operations.
 * Provides methods for user registration and login.
 */
public interface IAuthService {

    /**
     * Registers a new user in the system.
     *
     * @param request the registration details
     * @return authentication response with JWT token
     * @throws com.mathlearning.exception.InvalidRequestException if email already
     *                                                            exists
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Authenticates a user and provides JWT token.
     *
     * @param request the login credentials
     * @return authentication response with JWT token
     * @throws com.mathlearning.exception.InvalidRequestException if credentials are
     *                                                            invalid
     */
    AuthResponse login(LoginRequest request);
}
