package com.mathlearning.controller;

import com.mathlearning.model.Question;
import com.mathlearning.model.User;
import com.mathlearning.repository.QuestionRepository;
import com.mathlearning.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuestionController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("QuestionController Unit Tests")
class QuestionControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private QuestionRepository questionRepository;

        @MockBean
        private UserRepository userRepository;

        @MockBean
        private org.springframework.data.jpa.mapping.JpaMetamodelMappingContext jpaMetamodelMappingContext;

        private User testUser;
        private Question testQuestion1;
        private Question testQuestion2;

        @BeforeEach
        void setUp() {
                testUser = User.builder()
                                .id(1L)
                                .email("test@example.com")
                                .fullName("Test User")
                                .gradeLevel(8)
                                .build();

                testQuestion1 = Question.builder()
                                .id(1L)
                                .subject("Math")
                                .topic("Algebra")
                                .gradeLevel(8)
                                .questionText("Solve for x: 2x + 5 = 15")
                                .difficulty(Question.Difficulty.MEDIUM)
                                .build();

                testQuestion2 = Question.builder()
                                .id(2L)
                                .subject("Math")
                                .topic("Geometry")
                                .gradeLevel(8)
                                .questionText("Find the area of a circle with radius 5")
                                .difficulty(Question.Difficulty.EASY)
                                .build();
        }

        @Test
        @DisplayName("Should return daily questions for user's grade level")
        void getDailyQuestions_ReturnsQuestionsForGradeLevel() throws Exception {
                when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
                when(questionRepository.findByGradeLevel(anyInt()))
                                .thenReturn(Arrays.asList(testQuestion1, testQuestion2));

                mockMvc.perform(get("/api/questions/daily")
                                .with(authentication(
                                                new org.springframework.security.authentication.TestingAuthenticationToken(
                                                                1L, null))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data").isArray())
                                .andExpect(jsonPath("$.data.length()").value(2))
                                .andExpect(jsonPath("$.data[0].id").value(1))
                                .andExpect(jsonPath("$.data[0].subject").value("Math"))
                                .andExpect(jsonPath("$.data[0].topic").value("Algebra"))
                                .andExpect(jsonPath("$.data[0].gradeLevel").value(8))
                                .andExpect(jsonPath("$.data[0].difficulty").value("MEDIUM"))
                                .andExpect(jsonPath("$.data[1].id").value(2))
                                .andExpect(jsonPath("$.data[1].topic").value("Geometry"));
        }

        @Test
        @DisplayName("Should handle user not found")
        void getDailyQuestions_UserNotFound_ThrowsException() throws Exception {
                when(userRepository.findById(any())).thenReturn(Optional.empty());

                mockMvc.perform(get("/api/questions/daily")
                                .with(authentication(
                                                new org.springframework.security.authentication.TestingAuthenticationToken(
                                                                1L, null))))
                                .andExpect(status().is5xxServerError());
        }

        @Test
        @DisplayName("Should return empty list when no questions available")
        void getDailyQuestions_NoQuestions_ReturnsEmptyList() throws Exception {
                when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
                when(questionRepository.findByGradeLevel(anyInt())).thenReturn(Arrays.asList());

                mockMvc.perform(get("/api/questions/daily")
                                .with(authentication(
                                                new org.springframework.security.authentication.TestingAuthenticationToken(
                                                                1L, null))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data").isArray())
                                .andExpect(jsonPath("$.data.length()").value(0));
        }
}
