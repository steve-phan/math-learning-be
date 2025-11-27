package com.mathlearning.repository;

import com.mathlearning.model.Question;
import com.mathlearning.model.Submission;
import com.mathlearning.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("SubmissionRepository Tests")
class SubmissionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SubmissionRepository submissionRepository;

    private User testUser;
    private Question testQuestion;

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

        testQuestion = Question.builder()
                .subject("Math")
                .topic("Algebra")
                .gradeLevel(8)
                .questionText("Solve for x")
                .correctAnswer("x = 5")
                .difficulty(Question.Difficulty.MEDIUM)
                .build();
        entityManager.persist(testQuestion);
        entityManager.flush();
    }

    @Test
    @DisplayName("Should find submissions by user ID ordered by created date")
    void findByUserIdOrderByCreatedAtDesc_ReturnsOrderedSubmissions() {
        // given
        Submission submission1 = createSubmission(BigDecimal.valueOf(8.0), true);
        Submission submission2 = createSubmission(BigDecimal.valueOf(9.5), true);
        entityManager.persist(submission1);
        entityManager.persist(submission2);
        entityManager.flush();

        // when
        List<Submission> submissions = submissionRepository.findByUserIdOrderByCreatedAtDesc(testUser.getId());

        // then
        assertThat(submissions).hasSize(2);
        assertThat(submissions.get(0).getAiScore()).isEqualByComparingTo(BigDecimal.valueOf(9.5));
    }

    @Test
    @DisplayName("Should count submissions by user ID and correctness")
    void countByUserIdAndIsCorrect_ReturnsCorrectCount() {
        // given
        Submission correct1 = createSubmission(BigDecimal.valueOf(9.0), true);
        Submission correct2 = createSubmission(BigDecimal.valueOf(8.5), true);
        Submission incorrect = createSubmission(BigDecimal.valueOf(4.0), false);
        entityManager.persist(correct1);
        entityManager.persist(correct2);
        entityManager.persist(incorrect);
        entityManager.flush();

        // when
        long correctCount = submissionRepository.countByUserIdAndIsCorrect(testUser.getId(), true);
        long incorrectCount = submissionRepository.countByUserIdAndIsCorrect(testUser.getId(), false);

        // then
        assertThat(correctCount).isEqualTo(2);
        assertThat(incorrectCount).isEqualTo(1);
    }

    @Test
    @DisplayName("Should return zero count when no submissions exist")
    void countByUserIdAndIsCorrect_NoSubmissions_ReturnsZero() {
        // when
        long count = submissionRepository.countByUserIdAndIsCorrect(999L, true);

        // then
        assertThat(count).isZero();
    }

    @Test
    @DisplayName("Should save submission with all fields")
    void save_CompleteSubmission_SavesSuccessfully() {
        // given
        Submission submission = Submission.builder()
                .user(testUser)
                .question(testQuestion)
                .originalImageUrl("http://example.com/image.jpg")
                .aiScore(BigDecimal.valueOf(9.5))
                .isCorrect(true)
                .aiFeedback("Excellent work!")
                .processingTimeMs(1500)
                .aiProvider("GPT4O")
                .build();

        // when
        Submission saved = submissionRepository.save(submission);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getAiScore()).isEqualByComparingTo(BigDecimal.valueOf(9.5));
        assertThat(saved.getIsCorrect()).isTrue();
        assertThat(saved.getAiFeedback()).isEqualTo("Excellent work!");
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    private Submission createSubmission(BigDecimal score, boolean isCorrect) {
        return Submission.builder()
                .user(testUser)
                .question(testQuestion)
                .originalImageUrl("http://example.com/image.jpg")
                .aiScore(score)
                .isCorrect(isCorrect)
                .aiFeedback("Feedback")
                .processingTimeMs(1000)
                .aiProvider("GPT4O")
                .build();
    }
}
