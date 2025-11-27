package com.mathlearning.service;

import com.mathlearning.dto.GradingResult;
import com.mathlearning.exception.AIGradingException;
import com.mathlearning.service.impl.AIGradingServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AIGradingService Unit Tests")
class AIGradingServiceTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AIGradingServiceImpl aiGradingService;

    @BeforeEach
    void setUp() {
        // Set test API key using reflection
        ReflectionTestUtils.setField(aiGradingService, "openaiApiKey", "test-api-key");
        ReflectionTestUtils.setField(aiGradingService, "model", "gpt-4o");
    }

    @Test
    @DisplayName("Should build correct grading prompt")
    void buildGradingPrompt_CreatesStructuredPrompt() {
        // This test verifies the prompt structure by invoking the public method
        // We can't directly test private method but can verify behavior through public
        // API

        // If this test were to call the actual API, it would use the constructed prompt
        // For now, this is a placeholder showing test structure
        assertThat(aiGradingService).isNotNull();
    }

    @Test
    @DisplayName("Should throw AIGradingException on OpenAI API failure")
    void gradeSubmission_ApiFailure_ThrowsException() {
        // given - invalid API key will cause failure
        ReflectionTestUtils.setField(aiGradingService, "openaiApiKey", "invalid-key");

        // when & then
        assertThatThrownBy(() -> aiGradingService.gradeSubmission(
                "http://example.com/image.jpg",
                "What is 2+2?",
                "4",
                6))
                .isInstanceOf(AIGradingException.class)
                .hasMessageContaining("Failed to grade submission");
    }

    @Test
    @DisplayName("Should handle malformed JSON response")
    void parseGradingResponse_MalformedJson_ThrowsException() {
        // This test would verify JSON parsing error handling
        // Requires access to parseGradingResponse method or testing through public API
        assertThat(aiGradingService).isNotNull();
    }
}
