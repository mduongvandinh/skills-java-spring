# AGENTS.md

This project follows [OpenSpec](https://github.com/Fission-AI/OpenSpec) for **spec-first development**.

> "Agree on WHAT to build BEFORE writing any code"

## Spec-First Workflow

```
┌─────────┐    ┌─────────────┐    ┌───────────┐    ┌──────┐    ┌─────────┐
│  Draft  │───▶│Review/Align │───▶│ Implement │───▶│ Ship │───▶│ Archive │
│proposal │    │  with AI    │    │  tasks    │    │      │    │  specs  │
└─────────┘    └─────────────┘    └───────────┘    └──────┘    └─────────┘
```

### 1. Draft Proposal (Before Coding!)

When you need a new feature, **DON'T start coding**. First create:

```
openspec/changes/{feature-name}/
├── proposal.md    # WHY - Rationale and scope
├── tasks.md       # HOW - Implementation checklist
└── specs/         # WHAT - Delta specifications (ADDED/MODIFIED/REMOVED)
```

### 2. Review & Align

Discuss the proposal with AI assistant:
- Is the scope clear?
- Are there edge cases?
- Does it conflict with existing specs?

### 3. Implement

Only after alignment, follow `tasks.md` to implement.

### 4. Ship & Archive

After implementation:
- Merge delta specs into `openspec/specs/`
- Archive the change folder

## Project Structure

```
java-spring-skills/
├── openspec/
│   ├── specs/              # Current specifications
│   │   ├── architecture/   # Package structure, layers
│   │   ├── api/            # REST API standards
│   │   └── testing/        # Testing patterns
│   └── changes/            # Proposed changes
├── src/main/java/          # Application code
└── src/test/java/          # Tests
```

## Package Structure

```
com.company.app/
├── domain/           # Entities, Value Objects, Repositories
├── application/      # Services, Use Cases, DTOs
├── infrastructure/   # JPA, External APIs, Config
└── interfaces/       # Controllers, Request/Response
```

## Code Patterns

### Service Pattern
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
        var user = User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .build();
        return toDto(repository.save(user));
    }
}
```

### Controller Pattern
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

    @PostMapping
    public ResponseEntity<ApiResponse<UserDto>> create(
            @Valid @RequestBody CreateUserRequest request) {
        var user = userService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(user));
    }
}
```

### Test Pattern (TDD)
```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Should return user when valid ID provided")
    void shouldReturnUserWhenValidIdProvided() {
        // Given
        var user = User.builder().id(1L).username("john").build();
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        // When
        var result = userService.findById(1L);

        // Then
        assertThat(result.getUsername()).isEqualTo("john");
    }
}
```

## Quick Commands

```bash
# Development
mvn spring-boot:run
mvn spring-boot:run -Pdev

# Testing
mvn test
mvn test -Dtest=UserServiceTest

# Build
mvn clean package
mvn clean package -Pprod
```

## Maven Profiles

| Profile | Description |
|---------|-------------|
| `dev` | Development with H2 database |
| `prod` | Production with PostgreSQL |
| `docker` | Docker deployment |

## References

- Architecture: `openspec/specs/architecture/`
- API Standards: `openspec/specs/api/`
- Testing Guide: `openspec/specs/testing/`
