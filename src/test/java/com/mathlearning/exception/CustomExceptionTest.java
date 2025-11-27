package com.mathlearning.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Custom Exception Tests")
class CustomExceptionTest {

    @Test
    @DisplayName("ResourceNotFoundException should contain resource details")
    void resourceNotFoundException_ContainsDetails() {
        // when
        ResourceNotFoundException exception = new ResourceNotFoundException("User", "id", 123L);

        // then
        assertThat(exception.getMessage()).contains("User");
        assertThat(exception.getMessage()).contains("id");
        assertThat(exception.getMessage()).contains("123");
    }

    @Test
    @DisplayName("InvalidRequestException should contain message")
    void invalidRequestException_ContainsMessage() {
        // given
        String message = "Invalid email format";

        // when
        InvalidRequestException exception = new InvalidRequestException(message);

        // then
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("AIGradingException should contain message")
    void aiGradingException_ContainsMessage() {
        // given
        String message = "Failed to connect to AI service";

        // when
        AIGradingException exception = new AIGradingException(message);

        // then
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("AIGradingException should wrap cause")
    void aiGradingException_WrapsCause() {
        // given
        Exception cause = new RuntimeException("Connection timeout");

        // when
        AIGradingException exception = new AIGradingException("AI service error", cause);

        // then
        assertThat(exception.getMessage()).isEqualTo("AI service error");
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getCause().getMessage()).isEqualTo("Connection timeout");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException")
    void throwResourceNotFoundException() {
        assertThatThrownBy(() -> {
            throw new ResourceNotFoundException("Question", "id", 999L);
        })
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Question")
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("Should throw InvalidRequestException")
    void throwInvalidRequestException() {
        assertThatThrownBy(() -> {
            throw new InvalidRequestException("Email already exists");
        })
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("Email already exists");
    }

    @Test
    @DisplayName("Should throw AIGradingException")
    void throwAIGradingException() {
        assertThatThrownBy(() -> {
            throw new AIGradingException("Grading failed");
        })
                .isInstanceOf(AIGradingException.class)
                .hasMessage("Grading failed");
    }
}
