# Registration Specification (Delta)

## ADDED Requirements

### Requirement: User Registration Endpoint

The system SHALL provide a registration endpoint.

#### Scenario: Successful registration

Given a valid registration request
When POST /api/auth/register is called
Then the system SHALL:
- Create a new user with status PENDING
- Generate email verification token
- Send verification email
- Return 201 Created with user info (excluding password)

```json
// Request
POST /api/auth/register
{
  "username": "john",
  "email": "john@example.com",
  "password": "SecurePass123!"
}

// Response 201
{
  "success": true,
  "data": {
    "id": 1,
    "username": "john",
    "email": "john@example.com",
    "status": "PENDING_VERIFICATION"
  },
  "message": "Registration successful. Please verify your email."
}
```

---

### Requirement: Password Validation

Passwords SHALL meet security requirements.

#### Scenario: Password validation rules

Given a registration request
When validating password
Then password SHALL:
- Be at least 8 characters
- Contain at least 1 uppercase letter
- Contain at least 1 lowercase letter
- Contain at least 1 number
- Contain at least 1 special character

#### Scenario: Invalid password

Given password "weak"
When validating
Then return 400 Bad Request with validation errors

---

### Requirement: Email Verification

Users SHALL verify email before full access.

#### Scenario: Verify email

Given a valid verification token
When GET /api/auth/verify?token={token} is called
Then user status SHALL change to ACTIVE

#### Scenario: Expired token

Given an expired verification token (> 24 hours)
When verification is attempted
Then return 400 Bad Request with "Token expired"

---

### Requirement: Duplicate Prevention

The system SHALL prevent duplicate registrations.

#### Scenario: Email already exists

Given email "existing@example.com" is registered
When registration with same email is attempted
Then return 409 Conflict with "Email already registered"

#### Scenario: Username already exists

Given username "existinguser" is taken
When registration with same username is attempted
Then return 409 Conflict with "Username already taken"
