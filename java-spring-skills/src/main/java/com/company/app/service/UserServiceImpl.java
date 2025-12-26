package com.company.app.service;

import com.company.app.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory implementation of UserService.
 * For demo purposes - replace with JPA repository in production.
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final Map<Long, UserDto> users = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public UserServiceImpl() {
        // Add sample data
        create(UserDto.builder()
                .username("admin")
                .email("admin@example.com")
                .fullName("Administrator")
                .active(true)
                .build());
        create(UserDto.builder()
                .username("user1")
                .email("user1@example.com")
                .fullName("User One")
                .active(true)
                .build());
        log.info("UserServiceImpl initialized with sample data");
    }

    @Override
    public List<UserDto> findAll() {
        log.debug("Finding all users, count: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<UserDto> findById(Long id) {
        log.debug("Finding user by id: {}", id);
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public UserDto create(UserDto userDto) {
        Long id = idGenerator.getAndIncrement();
        userDto.setId(id);
        users.put(id, userDto);
        log.info("Created user: {}", userDto.getUsername());
        return userDto;
    }

    @Override
    public Optional<UserDto> update(Long id, UserDto userDto) {
        if (!users.containsKey(id)) {
            log.warn("User not found for update: {}", id);
            return Optional.empty();
        }
        userDto.setId(id);
        users.put(id, userDto);
        log.info("Updated user: {}", id);
        return Optional.of(userDto);
    }

    @Override
    public boolean delete(Long id) {
        if (users.remove(id) != null) {
            log.info("Deleted user: {}", id);
            return true;
        }
        log.warn("User not found for deletion: {}", id);
        return false;
    }
}
