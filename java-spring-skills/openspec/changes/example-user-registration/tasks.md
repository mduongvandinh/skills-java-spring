# Tasks: User Registration

## Implementation Checklist

### Database
- [ ] Create Flyway migration for `users` table
- [ ] Create Flyway migration for `email_verifications` table

### Domain Layer
- [ ] Create `User` entity
- [ ] Create `EmailVerification` entity
- [ ] Create `UserRepository` interface

### Application Layer
- [ ] Create `RegisterUserRequest` DTO
- [ ] Create `UserDto` response
- [ ] Create `UserRegistrationService`
- [ ] Create `EmailVerificationService`

### Interface Layer
- [ ] Create `AuthController` with `/register` endpoint
- [ ] Add validation annotations to request DTO

### Infrastructure
- [ ] Configure email service (SMTP)
- [ ] Create email templates

### Testing (TDD)
- [ ] Write unit tests for `UserRegistrationService`
- [ ] Write integration tests for registration flow
- [ ] Test email verification flow

### Documentation
- [ ] Update API documentation
- [ ] Add to openspec/specs after completion
