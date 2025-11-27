package com.mathlearning.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ApiResponse Tests")
class ApiResponseTest {

    @Test
    @DisplayName("Should create success response with data")
    void success_WithData_CreatesSuccessResponse() {
        // given
        String data = "Test Data";

        // when
        ApiResponse<String> response = ApiResponse.success(data);

        // then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isEqualTo("Test Data");
        assertThat(response.getMessage()).isNull();
    }

    @Test
    @DisplayName("Should create success response with message and data")
    void success_WithMessageAndData_CreatesSuccessResponse() {
        // given
        String message = "Operation successful";
        Integer data = 42;

        // when
        ApiResponse<Integer> response = ApiResponse.success(message, data);

        // then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Operation successful");
        assertThat(response.getData()).isEqualTo(42);
    }

    @Test
    @DisplayName("Should create error response")
    void error_WithMessage_CreatesErrorResponse() {
        // given
        String errorMessage = "Something went wrong";

        // when
        ApiResponse<Object> response = ApiResponse.error(errorMessage);

        // then
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("Something went wrong");
        assertThat(response.getData()).isNull();
    }

    @Test
    @DisplayName("Should handle null data in success response")
    void success_WithNullData_CreatesSuccessResponse() {
        // when
        ApiResponse<String> response = ApiResponse.success(null);

        // then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isNull();
    }

    @Test
    @DisplayName("Should create response using builder")
    void builder_CreatesCustomResponse() {
        // when
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Custom message")
                .data("Custom data")
                .build();

        // then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Custom message");
        assertThat(response.getData()).isEqualTo("Custom data");
    }
}
