# Claude Code Instructions

> This file provides context for Claude Code (CLI) and Claude in IDE extensions.

## Project Overview

Java Spring Boot application with modular architecture:
- **Database**: PostgreSQL + JPA/Hibernate + Flyway
- **Caching**: Redis (optional)
- **Messaging**: Kafka or RabbitMQ (optional)
- **Security**: JWT + OAuth2 (optional)
- **Testing**: TDD with Mockito

## Tech Stack

- Java 21, Spring Boot 3.2.x, Maven
- PostgreSQL 16, Redis 7, Kafka/RabbitMQ
- JUnit 5, Mockito, Testcontainers

## Code Conventions

### Naming
- Classes: `PascalCase` (UserService, OrderController)
- Methods/Variables: `camelCase` (findById, userName)
- Constants: `UPPER_SNAKE_CASE` (MAX_RETRY_COUNT)

### Structure
```
com.company.app/
├── config/       # @Configuration classes
├── controller/   # @RestController
├── service/      # Business logic interfaces + impl
├── repository/   # JpaRepository interfaces
├── domain/       # @Entity classes
├── dto/          # Data transfer objects
└── exception/    # Custom exceptions
```

### Best Practices
- Use Lombok: `@Data`, `@Builder`, `@RequiredArgsConstructor`
- Constructor injection (not @Autowired on fields)
- Return `Optional<T>` for nullable results
- Use `ApiResponse<T>` wrapper for REST responses

## TDD Workflow (Red-Green-Refactor)

1. **RED**: Write failing test first
2. **GREEN**: Minimal code to pass
3. **REFACTOR**: Improve while keeping green

```java
@Test
@DisplayName("Should return user when valid ID provided")
void shouldReturnUserWhenValidId() {
    // Given
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    // When
    Optional<UserDto> result = userService.findById(1L);

    // Then
    assertThat(result).isPresent();
}
```

## Module System

Enable modules via environment variables:
```bash
MODULE_REDIS_ENABLED=true
MODULE_KAFKA_ENABLED=true
MODULE_OAUTH2_ENABLED=true
```

Check enabled modules: `GET /api/system/modules`

## Commands

```bash
# Development
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Testing
mvn test

# Build
mvn clean package -DskipTests

# Docker
docker-compose up -d postgres
docker-compose --profile with-redis up -d
```

## API Conventions

- REST endpoints: `/api/{resource}` (plural nouns)
- Response format: `ApiResponse<T>` with data, message, success, timestamp
- Error format: error code, message, details array
- Pagination: `?page=0&size=20&sort=createdAt,desc`

## Security Notes

- Never commit secrets (.env, credentials)
- Use `@Value("${property}")` for config
- BCrypt for passwords
- JWT for stateless auth
