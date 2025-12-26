package com.company.gateway.fallback;

import com.company.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Fallback controller for circuit breaker.
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Void>> userServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(ApiResponse.error("User service is currently unavailable. Please try again later."));
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<Void>> orderServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(ApiResponse.error("Order service is currently unavailable. Please try again later."));
    }

    @GetMapping("/payments")
    public ResponseEntity<ApiResponse<Void>> paymentServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(ApiResponse.error("Payment service is currently unavailable. Please try again later."));
    }

    @GetMapping("/default")
    public ResponseEntity<ApiResponse<Void>> defaultFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(ApiResponse.error("Service is currently unavailable. Please try again later."));
    }
}
