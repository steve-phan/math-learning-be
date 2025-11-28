package com.mathlearning.controller;

import com.mathlearning.dto.SubmissionDto;
import com.mathlearning.dto.SubmissionResponse;
import com.mathlearning.service.ISubmissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SubmissionController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(com.mathlearning.config.SecurityConfig.class)
@TestPropertySource(properties = { "spring.flyway.enabled=false", "app.cors.allowed-origins=*" })
@DisplayName("SubmissionController Unit Tests")
class SubmissionControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private ISubmissionService submissionService;

        @MockBean
        private com.mathlearning.security.JwtTokenProvider jwtTokenProvider;

        @MockBean
        private org.springframework.data.jpa.mapping.JpaMetamodelMappingContext jpaMetamodelMappingContext;

        private SubmissionResponse submissionResponse;
        private SubmissionDto submissionDto;

        @BeforeEach
        void setUp() {
                submissionResponse = SubmissionResponse.builder()
                                .submissionId(1L)
                                .score(BigDecimal.valueOf(9.5))
                                .correct(true)
                                .feedback("Excellent work!")
                                .xpEarned(100)
                                .totalXp(500)
                                .currentStreak(5)
                                .processingTimeMs(1500)
                                .build();

                submissionDto = SubmissionDto.builder()
                                .id(1L)
                                .questionId(1L)
                                .questionText("What is 2+2?")
                                .score(BigDecimal.valueOf(9.5))
                                .correct(true)
                                .feedback("Great job!")
                                .createdAt(LocalDateTime.now())
                                .build();
        }

        @Test
        @DisplayName("Should successfully upload submission")
        void uploadSubmission_ValidImage_ReturnsSubmissionResponse() throws Exception {
                MockMultipartFile file = new MockMultipartFile(
                                "image",
                                "test.jpg",
                                "image/jpeg",
                                "test image content".getBytes());

                when(submissionService.createSubmission(any(), any(), any()))
                                .thenReturn(submissionResponse);

                mockMvc.perform(multipart("/api/submissions/upload")
                                .file(file)
                                .param("questionId", "1")
                                .with(authentication(
                                                new org.springframework.security.authentication.TestingAuthenticationToken(
                                                                1L, null))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.message").value("Submission graded successfully"))
                                .andExpect(jsonPath("$.data.submissionId").value(1))
                                .andExpect(jsonPath("$.data.score").value(9.5))
                                .andExpect(jsonPath("$.data.correct").value(true))
                                .andExpect(jsonPath("$.data.xpEarned").value(100))
                                .andExpect(jsonPath("$.data.totalXp").value(500));
        }

        @Test
        @DisplayName("Should handle submission upload error")
        void uploadSubmission_ServiceError_ReturnsBadRequest() throws Exception {
                MockMultipartFile file = new MockMultipartFile(
                                "image",
                                "test.jpg",
                                "image/jpeg",
                                "test image content".getBytes());

                when(submissionService.createSubmission(any(), any(), any()))
                                .thenThrow(new RuntimeException("Failed to process image"));

                mockMvc.perform(multipart("/api/submissions/upload")
                                .file(file)
                                .param("questionId", "1")
                                .with(authentication(
                                                new org.springframework.security.authentication.TestingAuthenticationToken(
                                                                1L, null))))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("Failed to process image"));
        }

        @Test
        @DisplayName("Should return submission history")
        void getHistory_ReturnsSubmissionList() throws Exception {
                List<SubmissionDto> submissions = Arrays.asList(submissionDto);
                when(submissionService.getUserSubmissions(any())).thenReturn(submissions);

                mockMvc.perform(get("/api/submissions/history")
                                .with(authentication(
                                                new org.springframework.security.authentication.TestingAuthenticationToken(
                                                                1L, null))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data").isArray())
                                .andExpect(jsonPath("$.data[0].id").value(1))
                                .andExpect(jsonPath("$.data[0].questionId").value(1))
                                .andExpect(jsonPath("$.data[0].correct").value(true));
        }

        @Test
        @DisplayName("Should return specific submission by ID")
        void getSubmission_ValidId_ReturnsSubmission() throws Exception {
                when(submissionService.getSubmission(any(), any())).thenReturn(submissionDto);

                mockMvc.perform(get("/api/submissions/1")
                                .with(authentication(
                                                new org.springframework.security.authentication.TestingAuthenticationToken(
                                                                1L, null))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data.id").value(1))
                                .andExpect(jsonPath("$.data.questionText").value("What is 2+2?"));
        }

        @Test
        @DisplayName("Should handle submission not found")
        void getSubmission_NotFound_ReturnsBadRequest() throws Exception {
                when(submissionService.getSubmission(any(), any()))
                                .thenThrow(new RuntimeException("Submission not found"));

                mockMvc.perform(get("/api/submissions/999")
                                .with(authentication(
                                                new org.springframework.security.authentication.TestingAuthenticationToken(
                                                                1L, null))))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.success").value(false));
        }
}
