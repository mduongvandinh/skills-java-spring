package com.company.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends BaseException {

    public ResourceNotFoundException(String message) {
        super(message, "NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    public ResourceNotFoundException(String resourceName, Long id) {
        super(
            String.format("%s not found with id: %d", resourceName, id),
            "NOT_FOUND",
            HttpStatus.NOT_FOUND
        );
    }

    public ResourceNotFoundException(String resourceName, String field, Object value) {
        super(
            String.format("%s not found with %s: %s", resourceName, field, value),
            "NOT_FOUND",
            HttpStatus.NOT_FOUND
        );
    }
}
