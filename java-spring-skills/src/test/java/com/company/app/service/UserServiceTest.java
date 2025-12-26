package com.company.app.service;

import com.company.app.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for UserServiceImpl.
 * Demonstrates TDD Red-Green-Refactor pattern.
 */
@DisplayName("UserService Tests")
class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl();
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("should return list of users including sample data")
        void shouldReturnListOfUsers() {
            // When
            List<UserDto> users = userService.findAll();

            // Then
            assertThat(users).isNotEmpty();
            assertThat(users.size()).isGreaterThanOrEqualTo(2); // Sample data
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("should return user when valid ID is provided")
        void shouldReturnUserWhenValidId() {
            // Given
            Long validId = 1L;

            // When
            Optional<UserDto> result = userService.findById(validId);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(validId);
        }

        @Test
        @DisplayName("should return empty when ID does not exist")
        void shouldReturnEmptyWhenIdNotFound() {
            // Given
            Long invalidId = 999L;

            // When
            Optional<UserDto> result = userService.findById(invalidId);

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("should create user and return with generated ID")
        void shouldCreateUserWithGeneratedId() {
            // Given
            UserDto newUser = UserDto.builder()
                    .username("newuser")
                    .email("newuser@example.com")
                    .fullName("New User")
                    .active(true)
                    .build();

            // When
            UserDto created = userService.create(newUser);

            // Then
            assertThat(created.getId()).isNotNull();
            assertThat(created.getUsername()).isEqualTo("newuser");
            assertThat(created.getEmail()).isEqualTo("newuser@example.com");
        }

        @Test
        @DisplayName("should be findable after creation")
        void shouldBeFindableAfterCreation() {
            // Given
            UserDto newUser = UserDto.builder()
                    .username("testuser")
                    .email("test@example.com")
                    .build();

            // When
            UserDto created = userService.create(newUser);
            Optional<UserDto> found = userService.findById(created.getId());

            // Then
            assertThat(found).isPresent();
            assertThat(found.get().getUsername()).isEqualTo("testuser");
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("should update existing user")
        void shouldUpdateExistingUser() {
            // Given
            Long existingId = 1L;
            UserDto updateData = UserDto.builder()
                    .username("updated")
                    .email("updated@example.com")
                    .fullName("Updated User")
                    .active(false)
                    .build();

            // When
            Optional<UserDto> result = userService.update(existingId, updateData);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getUsername()).isEqualTo("updated");
            assertThat(result.get().isActive()).isFalse();
        }

        @Test
        @DisplayName("should return empty when updating non-existent user")
        void shouldReturnEmptyWhenUpdatingNonExistent() {
            // Given
            Long nonExistentId = 999L;
            UserDto updateData = UserDto.builder()
                    .username("updated")
                    .email("updated@example.com")
                    .build();

            // When
            Optional<UserDto> result = userService.update(nonExistentId, updateData);

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("should delete existing user and return true")
        void shouldDeleteExistingUser() {
            // Given
            UserDto newUser = UserDto.builder()
                    .username("todelete")
                    .email("delete@example.com")
                    .build();
            UserDto created = userService.create(newUser);

            // When
            boolean result = userService.delete(created.getId());

            // Then
            assertThat(result).isTrue();
            assertThat(userService.findById(created.getId())).isEmpty();
        }

        @Test
        @DisplayName("should return false when deleting non-existent user")
        void shouldReturnFalseWhenDeletingNonExistent() {
            // Given
            Long nonExistentId = 999L;

            // When
            boolean result = userService.delete(nonExistentId);

            // Then
            assertThat(result).isFalse();
        }
    }
}
