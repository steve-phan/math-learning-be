package com.mathlearning.controller;

import com.mathlearning.dto.ProgressDto;
import com.mathlearning.dto.MistakeDto;
import com.mathlearning.model.MistakeNotebook;
import com.mathlearning.model.Submission;
import com.mathlearning.model.Question;
import com.mathlearning.model.UserProgress;
import com.mathlearning.repository.MistakeNotebookRepository;
import com.mathlearning.repository.SubmissionRepository;
import com.mathlearning.repository.UserProgressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProgressController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("ProgressController Unit Tests")
@SuppressWarnings("null")
class ProgressControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private UserProgressRepository userProgressRepository;

        @MockBean
        private SubmissionRepository submissionRepository;

        @MockBean
        private MistakeNotebookRepository mistakeNotebookRepository;

        @MockBean
        private org.springframework.data.jpa.mapping.JpaMetamodelMappingContext jpaMetamodelMappingContext;

        private UserProgress testProgress;
        private MistakeNotebook testMistake;

        @BeforeEach
        void setUp() {
                testProgress = UserProgress.builder()
                                .userId(1L)
                                .totalXp(500)
                                .currentStreak(5)
                                .longestStreak(10)
                                .build();

                Question question = Question.builder()
                                .id(1L)
                                .questionText("What is 2+2?")
                                .topic("Addition")
                                .build();

                Submission submission = Submission.builder()
                                .id(1L)
                                .question(question)
                                .build();

                testMistake = MistakeNotebook.builder()
                                .id(1L)
                                .submission(submission)
                                .reviewed(false)
                                .createdAt(LocalDateTime.now())
                                .build();
        }

        @Test
        @DisplayName("Should return user progress")
        void getProgress_ReturnsProgressDto() throws Exception {
                when(userProgressRepository.findByUserId(any())).thenReturn(Optional.of(testProgress));
                when(submissionRepository.countByUserIdAndIsCorrect(any(), anyBoolean()))
                                .thenReturn(8L) // correct (first call for true)
                                .thenReturn(12L) // incorrect (false)
                                .thenReturn(8L); // correct (second call for true)

                mockMvc.perform(get("/api/progress")
                                .with(authentication(
                                                new org.springframework.security.authentication.TestingAuthenticationToken(
                                                                1L, null))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data.totalXp").value(500))
                                .andExpect(jsonPath("$.data.currentStreak").value(5))
                                .andExpect(jsonPath("$.data.longestStreak").value(10))
                                .andExpect(jsonPath("$.data.totalSubmissions").value(20))
                                .andExpect(jsonPath("$.data.correctSubmissions").value(8))
                                .andExpect(jsonPath("$.data.accuracy").value(40.0));
        }

        @Test
        @DisplayName("Should return empty progress for new user")
        void getProgress_NewUser_ReturnsDefaultProgress() throws Exception {
                when(userProgressRepository.findByUserId(any())).thenReturn(Optional.empty());
                when(submissionRepository.countByUserIdAndIsCorrect(any(), anyBoolean())).thenReturn(0L);

                mockMvc.perform(get("/api/progress")
                                .with(authentication(
                                                new org.springframework.security.authentication.TestingAuthenticationToken(
                                                                1L, null))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data.totalXp").value(0))
                                .andExpect(jsonPath("$.data.currentStreak").value(0))
                                .andExpect(jsonPath("$.data.totalSubmissions").value(0));
        }

        @Test
        @DisplayName("Should return user mistakes")
        void getMistakes_ReturnsMistakeList() throws Exception {
                when(mistakeNotebookRepository.findByUserIdAndReviewed(any(), anyBoolean()))
                                .thenReturn(Arrays.asList(testMistake));

                mockMvc.perform(get("/api/progress/mistakes")
                                .with(authentication(
                                                new org.springframework.security.authentication.TestingAuthenticationToken(
                                                                1L, null))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data").isArray())
                                .andExpect(jsonPath("$.data[0].id").value(1))
                                .andExpect(jsonPath("$.data[0].questionText").value("What is 2+2?"))
                                .andExpect(jsonPath("$.data[0].topic").value("Addition"))
                                .andExpect(jsonPath("$.data[0].reviewed").value(false));
        }
}
