package com.mathlearning.exception;

import org.springframework.http.HttpStatus;

public class InvalidRequestException extends MathLearningException {
    public InvalidRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INVALID_REQUEST");
    }

    public InvalidRequestException(String message, Throwable cause) {
        super(message, cause, HttpStatus.BAD_REQUEST, "INVALID_REQUEST");
    }
}
