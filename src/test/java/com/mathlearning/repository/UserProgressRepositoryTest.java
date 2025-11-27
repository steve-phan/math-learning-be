package com.mathlearning.repository;

import com.mathlearning.model.User;
import com.mathlearning.model.UserProgress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("UserProgressRepository Tests")
class UserProgressRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserProgressRepository userProgressRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@example.com")
                .passwordHash("hashedPassword")
                .fullName("Test User")
                .gradeLevel(8)
                .authProvider(User.AuthProvider.EMAIL)
                .build();
        entityManager.persist(testUser);
        entityManager.flush();
    }

    @Test
    @DisplayName("Should find user progress by user ID")
    void findByUserId_ExistingUser_ReturnsProgress() {
        // given
        UserProgress progress = UserProgress.builder()
                .userId(testUser.getId())
                .user(testUser)
                .totalXp(100)
                .currentStreak(5)
                .longestStreak(10)
                .build();
        entityManager.persist(progress);
        entityManager.flush();

        // when
        Optional<UserProgress> found = userProgressRepository.findByUserId(testUser.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getTotalXp()).isEqualTo(100);
        assertThat(found.get().getCurrentStreak()).isEqualTo(5);
        assertThat(found.get().getLongestStreak()).isEqualTo(10);
    }

    @Test
    @DisplayName("Should return empty when user progress not found")
    void findByUserId_NonExistingUser_ReturnsEmpty() {
        // when
        Optional<UserProgress> found = userProgressRepository.findByUserId(999L);

        // then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should save user progress")
    void save_ValidProgress_SavesSuccessfully() {
        // given
        UserProgress progress = UserProgress.builder()
                .userId(testUser.getId())
                .user(testUser)
                .totalXp(200)
                .currentStreak(3)
                .longestStreak(7)
                .build();

        // when
        UserProgress saved = userProgressRepository.save(progress);

        // then
        assertThat(saved.getUserId()).isNotNull();
        assertThat(saved.getTotalXp()).isEqualTo(200);
        assertThat(saved.getUser().getId()).isEqualTo(testUser.getId());
    }

    @Test
    @DisplayName("Should update existing user progress")
    void save_UpdateProgress_UpdatesSuccessfully() {
        // given
        UserProgress progress = UserProgress.builder()
                .userId(testUser.getId())
                .user(testUser)
                .totalXp(100)
                .currentStreak(5)
                .longestStreak(10)
                .build();
        entityManager.persist(progress);
        entityManager.flush();

        // when
        progress.setTotalXp(150);
        progress.setCurrentStreak(6);
        UserProgress updated = userProgressRepository.save(progress);

        // then
        assertThat(updated.getTotalXp()).isEqualTo(150);
        assertThat(updated.getCurrentStreak()).isEqualTo(6);
    }
}
