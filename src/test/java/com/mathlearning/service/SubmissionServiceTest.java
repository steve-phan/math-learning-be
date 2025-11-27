package com.mathlearning.service;

import com.mathlearning.dto.GradingResult;
import com.mathlearning.dto.SubmissionDto;
import com.mathlearning.dto.SubmissionResponse;
import com.mathlearning.exception.InvalidRequestException;
import com.mathlearning.exception.ResourceNotFoundException;
import com.mathlearning.model.*;
import com.mathlearning.repository.*;
import com.mathlearning.service.impl.SubmissionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SubmissionService Unit Tests")
class SubmissionServiceTest {

        @Mock
        private SubmissionRepository submissionRepository;
        @Mock
        private QuestionRepository questionRepository;
        @Mock
        private UserRepository userRepository;
        @Mock
        private UserProgressRepository userProgressRepository;
        @Mock
        private MistakeNotebookRepository mistakeNotebookRepository;
        @Mock
        private IAIGradingService aiGradingService;
        @Mock
        private IStorageService storageService;
        @Mock
        private MultipartFile image;

        @InjectMocks
        private SubmissionServiceImpl submissionService;

        private User testUser;
        private Question testQuestion;
        private UserProgress testProgress;
        private GradingResult gradingResult;

        @BeforeEach
        void setUp() {
                testUser = User.builder()
                                .id(1L)
                                .email("test@example.com")
                                .fullName("Test User")
                                .gradeLevel(8)
                                .build();

                testQuestion = Question.builder()
                                .id(1L)
                                .questionText("What is 2 + 2?")
                                .correctAnswer("4")
                                .gradeLevel(8)
                                .difficulty(Question.Difficulty.EASY)
                                .build();

                testProgress = UserProgress.builder()

                                .userId(1L)
                                .user(testUser)
                                .totalXp(100)
                                .currentStreak(5)
                                .longestStreak(10)
                                .build();

                gradingResult = GradingResult.builder()
                                .score(BigDecimal.valueOf(9.5))
                                .correct(true)
                                .feedback("Excellent work!")
                                .correctSteps(Arrays.asList("Step 1", "Step 2"))
                                .topicTags(Arrays.asList("addition"))
                                .processingTimeMs(1500)
                                .aiProvider("GPT4O")
                                .build();
        }

        @Test
        @DisplayName("Should successfully create submission")
        void createSubmission_Success() throws java.io.IOException {
                // given
                when(image.isEmpty()).thenReturn(false);
                when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
                when(questionRepository.findById(1L)).thenReturn(Optional.of(testQuestion));
                when(storageService.uploadFile(any(), anyString())).thenReturn("https://storage.com/image.jpg");
                when(aiGradingService.gradeSubmission(anyString(), anyString(), anyString(), anyInt()))
                                .thenReturn(gradingResult);
                when(submissionRepository.save(any(Submission.class))).thenAnswer(invocation -> {
                        Submission sub = invocation.getArgument(0);
                        sub.setId(1L);
                        return sub;
                });
                when(userProgressRepository.findByUserId(1L)).thenReturn(Optional.of(testProgress));

                // when
                SubmissionResponse response = submissionService.createSubmission(1L, 1L, image);

                // then
                assertThat(response).isNotNull();
                assertThat(response.getSubmissionId()).isEqualTo(1L);
                assertThat(response.getScore()).isEqualByComparingTo(BigDecimal.valueOf(9.5));
                assertThat(response.getCorrect()).isTrue();
                assertThat(response.getXpEarned()).isGreaterThan(0);

                verify(submissionRepository).save(any(Submission.class));
                verify(userProgressRepository).save(any(UserProgress.class));
                verify(mistakeNotebookRepository, never()).save(any(MistakeNotebook.class)); // Correct answer
        }

        @Test
        @DisplayName("Should throw exception when image is empty")
        void createSubmission_EmptyImage_ThrowsException() {
                // given
                when(image.isEmpty()).thenReturn(true);

                // when & then
                assertThatThrownBy(() -> submissionService.createSubmission(1L, 1L, image))
                                .isInstanceOf(InvalidRequestException.class)
                                .hasMessageContaining("Image file is required");

                verify(submissionRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void createSubmission_UserNotFound_ThrowsException() {
                // given
                when(image.isEmpty()).thenReturn(false);
                when(userRepository.findById(1L)).thenReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> submissionService.createSubmission(1L, 1L, image))
                                .isInstanceOf(ResourceNotFoundException.class)
                                .hasMessageContaining("User not found");
        }

        @Test
        @DisplayName("Should throw exception when question not found")
        void createSubmission_QuestionNotFound_ThrowsException() {
                // given
                when(image.isEmpty()).thenReturn(false);
                when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
                when(questionRepository.findById(1L)).thenReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> submissionService.createSubmission(1L, 1L, image))
                                .isInstanceOf(ResourceNotFoundException.class)
                                .hasMessageContaining("Question not found");
        }

        @Test
        @DisplayName("Should add to mistake notebook when answer is incorrect")
        void createSubmission_IncorrectAnswer_AddsToMistakeNotebook() throws java.io.IOException {
                // given
                gradingResult.setCorrect(false);
                gradingResult.setScore(BigDecimal.valueOf(3.0));

                when(image.isEmpty()).thenReturn(false);
                when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
                when(questionRepository.findById(1L)).thenReturn(Optional.of(testQuestion));
                when(storageService.uploadFile(any(), anyString())).thenReturn("https://storage.com/image.jpg");
                when(aiGradingService.gradeSubmission(anyString(), anyString(), anyString(), anyInt()))
                                .thenReturn(gradingResult);
                when(submissionRepository.save(any(Submission.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));
                when(userProgressRepository.findByUserId(1L)).thenReturn(Optional.of(testProgress));

                // when
                SubmissionResponse response = submissionService.createSubmission(1L, 1L, image);

                // then
                assertThat(response.getCorrect()).isFalse();
                verify(mistakeNotebookRepository).save(any(MistakeNotebook.class));
        }

        @Test
        @DisplayName("Should get user submissions")
        void getUserSubmissions_ReturnsSubmissionList() {
                // given
                Submission submission1 = Submission.builder()
                                .id(1L)
                                .user(testUser)
                                .question(testQuestion)
                                .aiScore(BigDecimal.valueOf(9.5))
                                .build();

                when(submissionRepository.findByUserIdOrderByCreatedAtDesc(1L))
                                .thenReturn(Arrays.asList(submission1));

                // when
                List<SubmissionDto> submissions = submissionService.getUserSubmissions(1L);

                // then
                assertThat(submissions).hasSize(1);
                assertThat(submissions.get(0).getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should get submission by ID")
        void getSubmission_ValidOwner_ReturnsSubmission() {
                // given
                Submission submission = Submission.builder()
                                .id(1L)
                                .user(testUser)
                                .question(testQuestion)
                                .aiScore(BigDecimal.valueOf(9.5))
                                .build();

                when(submissionRepository.findById(1L)).thenReturn(Optional.of(submission));

                // when
                SubmissionDto result = submissionService.getSubmission(1L, 1L);

                // then
                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should throw exception when accessing other user's submission")
        void getSubmission_InvalidOwner_ThrowsException() {
                // given
                Submission submission = Submission.builder()
                                .id(1L)
                                .user(testUser)
                                .question(testQuestion)
                                .build();

                when(submissionRepository.findById(1L)).thenReturn(Optional.of(submission));

                // when & then - trying to access with different user ID
                assertThatThrownBy(() -> submissionService.getSubmission(1L, 999L))
                                .isInstanceOf(InvalidRequestException.class)
                                .hasMessageContaining("don't have permission");
        }
}
