# GitHub Copilot Instructions

## Project: Java Spring Boot Modular Application

### Tech Stack
- Java 21, Spring Boot 3.2.x, Maven
- PostgreSQL + JPA/Hibernate + Flyway
- Optional: Redis, Kafka, RabbitMQ, OAuth2
- Testing: JUnit 5, Mockito, Testcontainers

### Code Conventions

**Always use:**
- Lombok: `@Data`, `@Builder`, `@RequiredArgsConstructor`, `@Slf4j`
- Constructor injection (not field `@Autowired`)
- `Optional<T>` for nullable returns
- `ApiResponse<T>` wrapper for REST responses
- `@DisplayName` on all test methods
- Given-When-Then pattern in tests

**Package structure:**
```
com.company.app/
├── config/       # @Configuration
├── controller/   # @RestController
├── service/      # Interface + Impl
├── repository/   # JpaRepository
├── domain/       # @Entity
├── dto/          # DTOs
└── exception/    # Exceptions
```

### Code Templates

**Service:**
```java
@Slf4j
@Service
@RequiredArgsConstructor
public class XxxServiceImpl implements XxxService {
    private final XxxRepository repository;

    @Override
    public Optional<XxxDto> findById(Long id) {
        return repository.findById(id).map(this::toDto);
    }
}
```

**Controller:**
```java
@RestController
@RequestMapping("/api/xxx")
@RequiredArgsConstructor
public class XxxController {
    private final XxxService service;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<XxxDto>> getById(@PathVariable Long id) {
        return service.findById(id)
            .map(x -> ResponseEntity.ok(ApiResponse.success(x)))
            .orElse(ResponseEntity.notFound().build());
    }
}
```

**Unit Test:**
```java
@ExtendWith(MockitoExtension.class)
class XxxServiceTest {
    @Mock XxxRepository repository;
    @InjectMocks XxxServiceImpl service;

    @Test
    @DisplayName("Should do something when condition")
    void shouldDoSomethingWhenCondition() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        // When
        var result = service.findById(1L);

        // Then
        assertThat(result).isPresent();
    }
}
```

### TDD Workflow
1. **RED**: Write failing test first
2. **GREEN**: Minimal code to pass
3. **REFACTOR**: Improve while green

### Don't
- Don't use field injection
- Don't return null (use Optional)
- Don't skip @DisplayName
- Don't commit secrets
- Don't use System.out (use @Slf4j)
