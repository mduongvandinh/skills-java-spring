package com.company.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

/**
 * Exception thrown when validation fails.
 */
@Getter
public class ValidationException extends BaseException {

    private final Map<String, String> errors;

    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR", HttpStatus.BAD_REQUEST);
        this.errors = Map.of();
    }

    public ValidationException(Map<String, String> errors) {
        super("Validation failed", "VALIDATION_ERROR", HttpStatus.BAD_REQUEST);
        this.errors = errors;
    }

    public ValidationException(String field, String message) {
        super("Validation failed", "VALIDATION_ERROR", HttpStatus.BAD_REQUEST);
        this.errors = Map.of(field, message);
    }
}
