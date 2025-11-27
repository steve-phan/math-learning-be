package com.mathlearning.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MathLearningException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;

    public MathLearningException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public MathLearningException(String message, Throwable cause, HttpStatus status, String errorCode) {
        super(message, cause);
        this.status = status;
        this.errorCode = errorCode;
    }
}
