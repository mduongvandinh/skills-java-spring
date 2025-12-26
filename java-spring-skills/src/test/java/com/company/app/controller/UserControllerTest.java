package com.company.app.controller;

import com.company.app.dto.UserDto;
import com.company.app.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller tests using MockMvc and Mockito.
 * Demonstrates mocking dependencies in Spring MVC tests.
 */
@WebMvcTest(UserController.class)
@ActiveProfiles("test")
@DisplayName("UserController Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Nested
    @DisplayName("GET /api/users")
    class GetAllUsers {

        @Test
        @WithMockUser
        @DisplayName("should return list of users")
        void shouldReturnListOfUsers() throws Exception {
            // Given
            List<UserDto> users = List.of(
                    UserDto.builder().id(1L).username("user1").email("user1@test.com").build(),
                    UserDto.builder().id(2L).username("user2").email("user2@test.com").build()
            );
            when(userService.findAll()).thenReturn(users);

            // When & Then
            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(2));
        }
    }

    @Nested
    @DisplayName("GET /api/users/{id}")
    class GetUserById {

        @Test
        @WithMockUser
        @DisplayName("should return user when found")
        void shouldReturnUserWhenFound() throws Exception {
            // Given
            UserDto user = UserDto.builder()
                    .id(1L)
                    .username("testuser")
                    .email("test@example.com")
                    .build();
            when(userService.findById(1L)).thenReturn(Optional.of(user));

            // When & Then
            mockMvc.perform(get("/api/users/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.username").value("testuser"));
        }

        @Test
        @WithMockUser
        @DisplayName("should return 404 when not found")
        void shouldReturn404WhenNotFound() throws Exception {
            // Given
            when(userService.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            mockMvc.perform(get("/api/users/999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/users")
    class CreateUser {

        @Test
        @WithMockUser
        @DisplayName("should create user and return 201")
        void shouldCreateUserAndReturn201() throws Exception {
            // Given
            UserDto inputUser = UserDto.builder()
                    .username("newuser")
                    .email("new@example.com")
                    .fullName("New User")
                    .build();
            UserDto createdUser = UserDto.builder()
                    .id(1L)
                    .username("newuser")
                    .email("new@example.com")
                    .fullName("New User")
                    .build();
            when(userService.create(any(UserDto.class))).thenReturn(createdUser);

            // When & Then
            mockMvc.perform(post("/api/users")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(inputUser)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(1));
        }

        @Test
        @WithMockUser
        @DisplayName("should return 400 for invalid input")
        void shouldReturn400ForInvalidInput() throws Exception {
            // Given - missing required fields
            UserDto invalidUser = UserDto.builder()
                    .username("")  // Invalid: blank
                    .email("invalid-email")  // Invalid: not an email
                    .build();

            // When & Then
            mockMvc.perform(post("/api/users")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidUser)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/users/{id}")
    class UpdateUser {

        @Test
        @WithMockUser
        @DisplayName("should update user when found")
        void shouldUpdateUserWhenFound() throws Exception {
            // Given
            UserDto updateData = UserDto.builder()
                    .username("updated")
                    .email("updated@example.com")
                    .build();
            UserDto updatedUser = UserDto.builder()
                    .id(1L)
                    .username("updated")
                    .email("updated@example.com")
                    .build();
            when(userService.update(eq(1L), any(UserDto.class))).thenReturn(Optional.of(updatedUser));

            // When & Then
            mockMvc.perform(put("/api/users/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateData)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.username").value("updated"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/users/{id}")
    class DeleteUser {

        @Test
        @WithMockUser
        @DisplayName("should delete user and return 200")
        void shouldDeleteUserAndReturn200() throws Exception {
            // Given
            when(userService.delete(1L)).thenReturn(true);

            // When & Then
            mockMvc.perform(delete("/api/users/1").with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @WithMockUser
        @DisplayName("should return 404 when user not found")
        void shouldReturn404WhenNotFound() throws Exception {
            // Given
            when(userService.delete(999L)).thenReturn(false);

            // When & Then
            mockMvc.perform(delete("/api/users/999").with(csrf()))
                    .andExpect(status().isNotFound());
        }
    }
}
