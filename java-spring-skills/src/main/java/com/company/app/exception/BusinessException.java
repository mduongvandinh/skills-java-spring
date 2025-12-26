package com.company.app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base exception for business logic errors.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus status;

    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.status = HttpStatus.BAD_REQUEST;
    }

    public BusinessException(String errorCode, String message, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }

    // Common business exceptions
    public static BusinessException emailAlreadyExists(String email) {
        return new BusinessException(
            "EMAIL_EXISTS",
            "Email already registered: " + email,
            HttpStatus.CONFLICT
        );
    }

    public static BusinessException invalidCredentials() {
        return new BusinessException(
            "INVALID_CREDENTIALS",
            "Invalid email or password",
            HttpStatus.UNAUTHORIZED
        );
    }

    public static BusinessException accountNotVerified() {
        return new BusinessException(
            "ACCOUNT_NOT_VERIFIED",
            "Please verify your email address",
            HttpStatus.FORBIDDEN
        );
    }
}
