# Testing Specification (TDD)

## Overview

This specification defines Test-Driven Development patterns using Mockito.

---

### Requirement: TDD Workflow

Development SHALL follow Red-Green-Refactor cycle.

#### Scenario: Implementing new feature

Given a new feature requirement
When developing
Then follow this cycle:
1. **RED**: Write a failing test first
2. **GREEN**: Write minimal code to pass
3. **REFACTOR**: Improve code quality, keep tests green

---

### Requirement: Unit Test Structure

Unit tests SHALL use Mockito with AAA pattern.

#### Scenario: Service unit test

Given a service class
When writing unit tests
Then it SHALL:
- Use `@ExtendWith(MockitoExtension.class)`
- Mock all dependencies with `@Mock`
- Inject mocks with `@InjectMocks`
- Follow Arrange-Act-Assert (Given-When-Then)

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Should return user when valid ID provided")
    void shouldReturnUserWhenValidIdProvided() {
        // Given (Arrange)
        var user = User.builder()
            .id(1L)
            .username("john")
            .email("john@example.com")
            .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When (Act)
        var result = userService.findById(1L);

        // Then (Assert)
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("john");
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.findById(999L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("User");
    }
}
```

---

### Requirement: Test Naming

Test methods SHALL have descriptive names.

#### Scenario: Naming convention

Given a test method
When naming it
Then it SHALL:
- Start with `should`
- Describe expected behavior
- Use `@DisplayName` for readable output

```java
// Method name
void shouldCreateUserWhenValidRequestProvided()
void shouldThrowExceptionWhenEmailAlreadyExists()
void shouldReturnEmptyListWhenNoUsersFound()

// With @DisplayName
@Test
@DisplayName("Should create user when valid request provided")
void shouldCreateUserWhenValidRequestProvided() { }
```

---

### Requirement: Mockito Verification

Tests SHALL verify mock interactions.

#### Scenario: Verifying method calls

Given a service calls repository
When testing
Then verify the interaction

```java
@Test
void shouldSaveUserToRepository() {
    // Given
    var request = new CreateUserRequest("john", "john@example.com");
    when(userRepository.save(any(User.class)))
        .thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

    // When
    userService.create(request);

    // Then
    verify(userRepository).save(argThat(user ->
        user.getUsername().equals("john") &&
        user.getEmail().equals("john@example.com")
    ));
    verifyNoMoreInteractions(userRepository);
}
```

---

### Requirement: Exception Testing

Exception scenarios SHALL be tested.

#### Scenario: Testing thrown exceptions

Given a method that throws exception
When testing
Then use `assertThatThrownBy`

```java
@Test
@DisplayName("Should throw ValidationException when email exists")
void shouldThrowValidationExceptionWhenEmailExists() {
    // Given
    var request = new CreateUserRequest("john", "existing@example.com");
    when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

    // When & Then
    assertThatThrownBy(() -> userService.create(request))
        .isInstanceOf(ValidationException.class)
        .hasMessage("Email already exists");

    verify(userRepository, never()).save(any());
}
```

---

### Requirement: Integration Tests

Integration tests SHALL test full request flow.

#### Scenario: Controller integration test

Given a REST endpoint
When testing end-to-end
Then use `@SpringBootTest` with `TestRestTemplate`

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should create user via POST endpoint")
    void shouldCreateUserViaPostEndpoint() {
        // Given
        var request = new CreateUserRequest("john", "john@example.com", "password123");

        // When
        var response = restTemplate.postForEntity(
            "/api/users",
            request,
            new ParameterizedTypeReference<ApiResponse<UserDto>>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getData().getUsername()).isEqualTo("john");
    }
}
```

---

### Requirement: Test Coverage

Tests SHALL cover critical paths.

#### Scenario: Coverage requirements

Given a service class
When reviewing test coverage
Then it SHALL cover:
- Happy path (success scenarios)
- Edge cases (empty, null, boundary values)
- Error scenarios (exceptions, validation failures)
- All public methods
