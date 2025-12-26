package com.company.app.service;

import com.company.app.dto.UserDto;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for User operations.
 */
public interface UserService {

    List<UserDto> findAll();

    Optional<UserDto> findById(Long id);

    UserDto create(UserDto userDto);

    Optional<UserDto> update(Long id, UserDto userDto);

    boolean delete(Long id);
}
