package com.mathlearning.repository;

import com.mathlearning.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("UserRepository Tests")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should find user by email")
    void findByEmail_ExistingEmail_ReturnsUser() {
        // given
        User user = User.builder()
                .email("test@example.com")
                .passwordHash("hashedPassword")
                .fullName("Test User")
                .gradeLevel(8)
                .authProvider(User.AuthProvider.EMAIL)
                .build();
        entityManager.persist(user);
        entityManager.flush();

        // when
        Optional<User> found = userRepository.findByEmail("test@example.com");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
        assertThat(found.get().getFullName()).isEqualTo("Test User");
    }

    @Test
    @DisplayName("Should return empty when email not found")
    void findByEmail_NonExistingEmail_ReturnsEmpty() {
        // when
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        // then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should check if email exists")
    void existsByEmail_ExistingEmail_ReturnsTrue() {
        // given
        User user = User.builder()
                .email("existing@example.com")
                .passwordHash("hashedPassword")
                .fullName("Existing User")
                .gradeLevel(8)
                .authProvider(User.AuthProvider.EMAIL)
                .build();
        entityManager.persist(user);
        entityManager.flush();

        // when
        boolean exists = userRepository.existsByEmail("existing@example.com");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when email does not exist")
    void existsByEmail_NonExistingEmail_ReturnsFalse() {
        // when
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should save new user")
    void save_NewUser_SavesSuccessfully() {
        // given
        User user = User.builder()
                .email("newuser@example.com")
                .passwordHash("hashedPassword")
                .fullName("New User")
                .gradeLevel(9)
                .authProvider(User.AuthProvider.EMAIL)
                .build();

        // when
        User saved = userRepository.save(user);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("newuser@example.com");
        assertThat(saved.getGradeLevel()).isEqualTo(9);
    }

    @Test
    @DisplayName("Should handle different auth providers")
    void save_GoogleAuthProvider_SavesSuccessfully() {
        // given
        User user = User.builder()
                .email("google@example.com")
                .fullName("Google User")
                .gradeLevel(7)
                .authProvider(User.AuthProvider.GOOGLE)
                .build();

        // when
        User saved = userRepository.save(user);

        // then
        assertThat(saved.getAuthProvider()).isEqualTo(User.AuthProvider.GOOGLE);
        assertThat(saved.getPasswordHash()).isNull();
    }
}
