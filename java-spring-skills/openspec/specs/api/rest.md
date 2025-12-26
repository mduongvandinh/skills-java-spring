# REST API Specification

## Overview

This specification defines REST API standards for the application.

---

### Requirement: Response Wrapper

All API responses SHALL use ApiResponse wrapper.

#### Scenario: Successful response

Given a successful API call
When returning the response
Then it SHALL be wrapped in ApiResponse

```java
@Data
@Builder
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .data(data)
            .timestamp(LocalDateTime.now())
            .build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
            .success(false)
            .message(message)
            .timestamp(LocalDateTime.now())
            .build();
    }
}
```

```json
{
  "success": true,
  "data": { "id": 1, "username": "john" },
  "message": null,
  "timestamp": "2024-12-26T10:00:00"
}
```

---

### Requirement: Error Response

Errors SHALL include detailed information.

#### Scenario: Validation error

Given a validation failure
When returning the error
Then it SHALL include field-level details

```json
{
  "success": false,
  "data": null,
  "message": "Validation failed",
  "errors": [
    { "field": "email", "message": "must be a valid email address" },
    { "field": "username", "message": "must not be blank" }
  ],
  "timestamp": "2024-12-26T10:00:00"
}
```

---

### Requirement: HTTP Status Codes

APIs SHALL use appropriate HTTP status codes.

#### Scenario: Status code mapping

| Operation | Success | Error |
|-----------|---------|-------|
| GET resource | 200 OK | 404 Not Found |
| POST create | 201 Created | 400 Bad Request |
| PUT update | 200 OK | 404 Not Found |
| DELETE | 204 No Content | 404 Not Found |
| Validation | - | 400 Bad Request |
| Auth | - | 401 Unauthorized |
| Server error | - | 500 Internal Server Error |

---

### Requirement: URL Conventions

URLs SHALL follow REST conventions.

#### Scenario: Resource URLs

Given a resource "User"
When defining endpoints
Then URLs SHALL be:

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/users` | List all users |
| GET | `/api/users/{id}` | Get user by ID |
| POST | `/api/users` | Create user |
| PUT | `/api/users/{id}` | Update user |
| DELETE | `/api/users/{id}` | Delete user |
| GET | `/api/users/{id}/orders` | Get user's orders |

---

### Requirement: Pagination

List endpoints SHALL support pagination.

#### Scenario: Paginated response

Given GET /api/users?page=0&size=20
When returning the list
Then it SHALL include pagination metadata

```java
@Data
@Builder
public class PageResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
}
```

```json
{
  "success": true,
  "data": {
    "content": [...],
    "page": 0,
    "size": 20,
    "totalElements": 150,
    "totalPages": 8,
    "first": true,
    "last": false
  }
}
```

---

### Requirement: Request Validation

Request bodies SHALL be validated.

#### Scenario: Create request validation

Given a create request
When validating input
Then it SHALL use Bean Validation

```java
@Data
public class CreateUserRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Must be valid email")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}
```

---

### Requirement: Global Exception Handler

Exceptions SHALL be handled globally.

#### Scenario: Exception handling

Given any exception occurs
When processing the request
Then GlobalExceptionHandler SHALL return appropriate error response

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> new FieldError(e.getField(), e.getDefaultMessage()))
            .toList();
        return ResponseEntity.badRequest()
            .body(ApiResponse.error("Validation failed", errors));
    }
}
```
