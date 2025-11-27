package com.mathlearning.service;

import com.mathlearning.dto.AuthResponse;
import com.mathlearning.dto.LoginRequest;
import com.mathlearning.dto.RegisterRequest;
import com.mathlearning.exception.InvalidRequestException;
import com.mathlearning.model.User;
import com.mathlearning.model.UserProgress;
import com.mathlearning.repository.UserProgressRepository;
import com.mathlearning.repository.UserRepository;
import com.mathlearning.security.IJwtTokenProvider;
import com.mathlearning.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserProgressRepository userProgressRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private IJwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .email("test@example.com")
                .password("password123")
                .fullName("Test User")
                .gradeLevel(8)
                .build();

        loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .passwordHash("encodedPassword")
                .fullName("Test User")
                .gradeLevel(8)
                .authProvider(User.AuthProvider.EMAIL)
                .build();
    }

    @Test
    @DisplayName("Should successfully register new user")
    void registerNewUser_Success() {
        // given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userProgressRepository.save(any(UserProgress.class))).thenReturn(new UserProgress());
        when(jwtTokenProvider.generateToken(anyLong(), anyString())).thenReturn("test-jwt-token");

        // when
        AuthResponse response = authService.register(registerRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("test-jwt-token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(response.getFullName()).isEqualTo(testUser.getFullName());

        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(userRepository).save(any(User.class));
        verify(userProgressRepository).save(any(UserProgress.class));
        verify(jwtTokenProvider).generateToken(testUser.getId(), testUser.getEmail());
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void registerNewUser_EmailExists_ThrowsException() {
        // given
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("Email already registered");

        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should successfully login with valid credentials")
    void login_ValidCredentials_Success() {
        // given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtTokenProvider.generateToken(anyLong(), anyString())).thenReturn("test-jwt-token");

        // when
        AuthResponse response = authService.login(loginRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("test-jwt-token");
        assertThat(response.getEmail()).isEqualTo(testUser.getEmail());

        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder).matches(loginRequest.getPassword(), testUser.getPasswordHash());
        verify(jwtTokenProvider).generateToken(testUser.getId(), testUser.getEmail());
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void login_UserNotFound_ThrowsException() {
        // given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("Invalid email or password");

        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception when password is incorrect")
    void login_InvalidPassword_ThrowsException() {
        // given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("Invalid email or password");

        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder).matches(loginRequest.getPassword(), testUser.getPasswordHash());
        verify(jwtTokenProvider, never()).generateToken(anyLong(), anyString());
    }
}
