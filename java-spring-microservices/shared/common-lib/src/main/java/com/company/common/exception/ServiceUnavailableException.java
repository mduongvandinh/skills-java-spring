package com.company.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when an external service is unavailable.
 */
public class ServiceUnavailableException extends BaseException {

    public ServiceUnavailableException(String serviceName) {
        super(
            String.format("Service '%s' is currently unavailable", serviceName),
            "SERVICE_UNAVAILABLE",
            HttpStatus.SERVICE_UNAVAILABLE
        );
    }

    public ServiceUnavailableException(String serviceName, Throwable cause) {
        super(
            String.format("Service '%s' is currently unavailable", serviceName),
            "SERVICE_UNAVAILABLE",
            HttpStatus.SERVICE_UNAVAILABLE,
            cause
        );
    }
}
