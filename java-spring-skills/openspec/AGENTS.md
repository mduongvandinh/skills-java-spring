# Agent Instructions for This Project

## Project Overview

Java Spring Boot application with modular architecture supporting:
- **Database**: PostgreSQL with JPA/Hibernate + Flyway migration
- **Caching**: Redis (optional)
- **Messaging**: Kafka or RabbitMQ (optional, choose one)
- **Security**: JWT authentication + OAuth2 (optional)
- **Testing**: TDD with Mockito + Testcontainers

## Tech Stack

| Component | Technology | Version |
|-----------|------------|---------|
| Language | Java | 21 |
| Framework | Spring Boot | 3.2.x |
| Build Tool | Maven | 3.9+ |
| Database | PostgreSQL | 16 |
| Cache | Redis | 7 |
| Messaging | Kafka / RabbitMQ | Latest |
| Testing | JUnit 5 + Mockito | Latest |

## Conventions

### Code Style
- Use **Lombok** for boilerplate reduction (`@Data`, `@Builder`, `@RequiredArgsConstructor`)
- Follow **Clean Architecture** principles
- Use **constructor injection** (not field injection)
- Prefer **immutable objects** where possible

### Naming Conventions
- Classes: `PascalCase` (e.g., `UserService`, `OrderController`)
- Methods/Variables: `camelCase` (e.g., `findById`, `userName`)
- Constants: `UPPER_SNAKE_CASE` (e.g., `MAX_RETRY_COUNT`)
- Packages: `lowercase` (e.g., `com.company.app.service`)

### Package Structure
```
com.company.app/
├── config/           # Configuration classes
├── controller/       # REST controllers
├── service/          # Business logic
├── repository/       # Data access
├── domain/           # Entities
├── dto/              # Data transfer objects
├── exception/        # Custom exceptions
├── security/         # Security configuration
└── util/             # Utility classes
```

## Development Workflow

### 1. Spec-First Approach
Before implementing any feature:
1. Check if specification exists in `openspec/specs/`
2. If not, create a proposal in `openspec/changes/<feature-name>/`
3. Get alignment on the spec before coding
4. Reference specs in code comments with `@spec` tag

### 2. TDD with Mockito
Follow the **Red-Green-Refactor** cycle:
1. **RED**: Write a failing test first
2. **GREEN**: Write minimal code to pass the test
3. **REFACTOR**: Improve code quality while keeping tests green

### 3. Code Review Checklist
- [ ] Follows naming conventions
- [ ] Has appropriate tests (unit + integration)
- [ ] No security vulnerabilities
- [ ] Spec reference included in comments
- [ ] No hardcoded values (use configuration)

## OpenSpec Commands

| Command | Description |
|---------|-------------|
| `/openspec-proposal <name>` | Create new feature proposal |
| `/openspec-review` | Review and align on specs |
| `/openspec-implement <name>` | Implement from approved spec |
| `/openspec-archive <name>` | Archive completed feature |

## Module Selection

This project uses **optional modules**. Check which modules are enabled:

```bash
# Check current module status
curl http://localhost:8080/api/system/modules
```

### Enable Modules

```bash
# Via Maven
mvn spring-boot:run -Dmodule.redis.enabled=true

# Via environment variable
MODULE_REDIS_ENABLED=true mvn spring-boot:run

# Via application.yml
modules:
  redis:
    enabled: true
```

## API Conventions

### REST Endpoints
- Use plural nouns: `/api/users`, `/api/orders`
- Use HTTP methods correctly: GET, POST, PUT, DELETE
- Return appropriate status codes: 200, 201, 400, 401, 404, 500
- Use pagination for lists: `?page=0&size=20`

### Response Format

```json
{
  "data": { ... },
  "message": "Success",
  "timestamp": "2024-12-25T10:00:00Z"
}
```

### Error Response Format

```json
{
  "error": "VALIDATION_ERROR",
  "message": "Invalid input",
  "details": [
    { "field": "email", "message": "must be valid email" }
  ],
  "timestamp": "2024-12-25T10:00:00Z"
}
```

## Testing Guidelines

### Unit Tests
- Location: `src/test/java/.../unit/`
- Mock all external dependencies
- Use `@ExtendWith(MockitoExtension.class)`
- One assertion per test when possible

### Integration Tests
- Location: `src/test/java/.../integration/`
- Use `@SpringBootTest`
- Use Testcontainers for database
- Test actual HTTP endpoints with `TestRestTemplate`

### Test Naming
```java
@Test
@DisplayName("Should return user when valid ID is provided")
void shouldReturnUserWhenValidIdProvided() { ... }
```

## Security Guidelines

### Never commit:
- API keys or secrets
- Database passwords
- JWT signing keys
- `.env` files with real values

### Always use:
- Environment variables for secrets
- `@Value("${property}")` for configuration
- BCrypt for password hashing
- HTTPS in production

## Useful Commands

```bash
# Development
make dev              # Run without Docker
make dev-docker       # Run with Docker infrastructure
make dev-full         # Run with all modules

# Testing
make test             # Run all tests
mvn test -Dtest=UserServiceTest  # Run specific test

# Build
make build            # Build minimal
make build-full       # Build with all modules

# Database
make db-migrate       # Run migrations
make db-clean         # Clean and migrate
```

## References

- [OpenSpec Documentation](https://github.com/Fission-AI/OpenSpec)
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Mockito Documentation](https://site.mockito.org/)
- [Testcontainers Guide](https://www.testcontainers.org/)
