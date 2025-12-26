# API Contracts Specification

## Overview

This specification defines REST API contracts for all core services.
OpenAPI specs are stored in `shared/api-contracts/`.

---

### Requirement: OpenAPI Standard

All APIs SHALL be documented using OpenAPI 3.0.

#### Scenario: API documentation

Given a core service
When documenting its API
Then the spec SHALL be in `shared/api-contracts/{service}-service.yaml`
And follow OpenAPI 3.0 specification

---

### Requirement: Response Wrapper

All API responses SHALL use ApiResponse wrapper.

#### Scenario: Successful response

Given a successful API call
When returning the response
Then it SHALL be wrapped in ApiResponse

```json
{
  "success": true,
  "data": { ... },
  "message": null,
  "timestamp": "2024-12-26T10:00:00Z"
}
```

#### Scenario: Error response

Given a failed API call
When returning the error
Then it SHALL include error details

```json
{
  "success": false,
  "data": null,
  "message": "User not found",
  "timestamp": "2024-12-26T10:00:00Z"
}
```

---

### Requirement: Standard Endpoints

Core services SHALL implement standard CRUD endpoints.

#### Scenario: User service endpoints

Given user-service
When defining endpoints
Then it SHALL include:

| Method | Path | Description |
|--------|------|-------------|
| GET | /api/users | List users (paginated) |
| GET | /api/users/{id} | Get user by ID |
| POST | /api/users | Create user |
| PUT | /api/users/{id} | Update user |
| DELETE | /api/users/{id} | Delete user |

---

### Requirement: Pagination

List endpoints SHALL support pagination.

#### Scenario: Paginated list

Given GET /api/users?page=0&size=20
When the response is returned
Then it SHALL include pagination metadata

```json
{
  "success": true,
  "data": {
    "content": [...],
    "page": 0,
    "size": 20,
    "totalElements": 150,
    "totalPages": 8
  }
}
```

---

### Requirement: Validation Errors

Validation errors SHALL return 400 with details.

#### Scenario: Invalid request

Given POST /api/users with invalid email
When validation fails
Then response SHALL be 400 Bad Request

```json
{
  "success": false,
  "message": "Validation failed",
  "errors": [
    {
      "field": "email",
      "message": "must be a valid email address"
    }
  ],
  "timestamp": "2024-12-26T10:00:00Z"
}
```

---

### Requirement: Error Status Codes

APIs SHALL use appropriate HTTP status codes.

#### Scenario: Status code mapping

| Condition | Status Code |
|-----------|-------------|
| Success | 200 OK |
| Created | 201 Created |
| No Content | 204 No Content |
| Bad Request | 400 Bad Request |
| Unauthorized | 401 Unauthorized |
| Forbidden | 403 Forbidden |
| Not Found | 404 Not Found |
| Conflict | 409 Conflict |
| Internal Error | 500 Internal Server Error |
| Service Unavailable | 503 Service Unavailable |

---

### Requirement: Request Headers

APIs SHALL require standard headers.

#### Scenario: Required headers

Given any API request
When calling the endpoint
Then these headers SHALL be supported:
- `Content-Type: application/json`
- `X-Request-ID: {uuid}` (optional, for tracing)
- `Authorization: Bearer {token}` (when auth enabled)

---

### Requirement: Health Endpoints

All services SHALL expose actuator health endpoints.

#### Scenario: Health check

Given any service
When calling GET /actuator/health
Then response SHALL indicate service health

```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "kafka": { "status": "UP" }
  }
}
```
