package com.mathlearning.exception;

import org.springframework.http.HttpStatus;

public class AIGradingException extends MathLearningException {
    public AIGradingException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, "AI_GRADING_ERROR");
    }

    public AIGradingException(String message, Throwable cause) {
        super(message, cause, HttpStatus.INTERNAL_SERVER_ERROR, "AI_GRADING_ERROR");
    }
}
