# Architecture Specification

## Overview

This specification defines the layered architecture for the monolithic Spring Boot application.

---

### Requirement: Package Structure

The application SHALL follow DDD-inspired package structure.

#### Scenario: New module created

Given a new feature module
When organizing packages
Then the structure SHALL be:
```
com.company.app/
├── domain/           # Entities, Value Objects, Repository interfaces
├── application/      # Services, Use Cases, DTOs, Mappers
├── infrastructure/   # JPA implementations, External APIs, Config
└── interfaces/       # REST Controllers, Request/Response objects
```

---

### Requirement: Domain Layer

The domain layer SHALL contain core business logic.

#### Scenario: Creating an entity

Given a business entity "User"
When implementing the entity
Then it SHALL:
- Be in `domain/` package
- Use JPA annotations
- Use Lombok for boilerplate
- Have builder pattern

```java
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
```

---

### Requirement: Application Layer

The application layer SHALL contain business services.

#### Scenario: Creating a service

Given a business operation
When implementing the service
Then it SHALL:
- Be annotated with `@Service`
- Use `@RequiredArgsConstructor` for injection
- Use `@Transactional` appropriately
- Return DTOs, not entities

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository repository;

    public UserDto findById(Long id) {
        return repository.findById(id)
            .map(this::toDto)
            .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    @Transactional
    public UserDto create(CreateUserRequest request) {
        var user = toEntity(request);
        return toDto(repository.save(user));
    }
}
```

---

### Requirement: Infrastructure Layer

The infrastructure layer SHALL contain technical implementations.

#### Scenario: Repository implementation

Given a repository interface in domain
When implementing with JPA
Then it SHALL:
- Extend `JpaRepository`
- Be in `infrastructure/` package
- Use custom query methods when needed

```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

---

### Requirement: Interface Layer

The interface layer SHALL handle HTTP communication.

#### Scenario: Creating a controller

Given a REST endpoint
When implementing the controller
Then it SHALL:
- Be annotated with `@RestController`
- Use `@RequestMapping` for base path
- Delegate to service layer
- Return `ApiResponse<T>` wrapper

```java
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.findById(id)));
    }
}
```

---

### Requirement: Dependency Direction

Dependencies SHALL flow inward.

#### Scenario: Layer dependencies

Given the layered architecture
When checking dependencies
Then:
- `interfaces` MAY depend on `application`
- `application` MAY depend on `domain`
- `infrastructure` MAY depend on `domain`
- `domain` SHALL NOT depend on other layers
