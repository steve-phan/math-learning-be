package com.mathlearning.service.impl;

import com.mathlearning.dto.AuthResponse;
import com.mathlearning.dto.LoginRequest;
import com.mathlearning.dto.RegisterRequest;
import com.mathlearning.exception.InvalidRequestException;
import com.mathlearning.model.User;
import com.mathlearning.model.UserProgress;
import com.mathlearning.repository.UserProgressRepository;
import com.mathlearning.repository.UserRepository;
import com.mathlearning.security.JwtTokenProvider;
import com.mathlearning.service.IAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final UserProgressRepository userProgressRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.debug("Attempting to register user with email: {}", request.getEmail());

        // Validate and check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new InvalidRequestException("Email already registered: " + request.getEmail());
        }

        // Create new user
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .gradeLevel(request.getGradeLevel())
                .authProvider(User.AuthProvider.EMAIL)
                .build();

        user = userRepository.save(user);
        log.info("User registered successfully: {}", user.getEmail());

        // Initialize user progress
        UserProgress progress = UserProgress.builder()
                .userId(user.getId())
                .user(user)
                .totalXp(0)
                .currentStreak(0)
                .longestStreak(0)
                .build();

        userProgressRepository.save(progress);

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .gradeLevel(user.getGradeLevel())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        log.debug("Attempting login for email: {}", request.getEmail());

        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidRequestException("Invalid email or password"));

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidRequestException("Invalid email or password");
        }

        log.info("User logged in successfully: {}", user.getEmail());

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .gradeLevel(user.getGradeLevel())
                .build();
    }
}
