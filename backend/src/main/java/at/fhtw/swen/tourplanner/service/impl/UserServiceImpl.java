package at.fhtw.swen.tourplanner.service.impl;

import at.fhtw.swen.tourplanner.persistence.entity.User;
import at.fhtw.swen.tourplanner.persistence.repository.adapter.UserRepositoryAdapter;
import at.fhtw.swen.tourplanner.security.core.PasswordHasher;
import at.fhtw.swen.tourplanner.service.UserService;
import at.fhtw.swen.tourplanner.service.dto.UserDto;
import at.fhtw.swen.tourplanner.controller.exception.UserDeletionException;
import at.fhtw.swen.tourplanner.controller.exception.UserRegistrationException;
import at.fhtw.swen.tourplanner.controller.exception.UserValidationException;
import at.fhtw.swen.tourplanner.service.mapper.UserMapper;
import at.fhtw.swen.tourplanner.util.LoggerService;
import at.fhtw.swen.tourplanner.util.UUIDv7;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final Logger logger;
    private final UserRepositoryAdapter userRepositoryAdapter;
    private final PasswordHasher passwordHasher;

    public UserServiceImpl(
            UserRepositoryAdapter userRepositoryAdapter,
            PasswordHasher passwordHasher
    ) {
        this.userRepositoryAdapter = userRepositoryAdapter;
        this.passwordHasher = passwordHasher;
        this.logger = LoggerService.getInstance().getLogger(getClass());
    }

    @Override
    public UserDto register(String username, String password, String email) {
        logger.info("Register attempt for username={}", username);

        validateCredentials(username, password);

        if (userRepositoryAdapter.existsByUsername(username)) {
            throw new UserRegistrationException("Username already exists");
        }

        String passwordHash = passwordHasher.hash(password);

        UserDto userDto = new UserDto(
                UUIDv7.randomUUID(),
                username,
                passwordHash,
                email,
                Instant.now()
        );

        User entity = UserMapper.toEntity(userDto);
        User saved = userRepositoryAdapter.save(entity);

        UserDto result = UserMapper.toDto(saved);

        logger.info("User registered: id={}, username={}", result.id(), result.username());
        return result;
    }

    @Override
    public void deleteUser(UUID uuid) {
        if (!userRepositoryAdapter.existsById(uuid)) {
            throw new UserDeletionException("User not found");
        }

        userRepositoryAdapter.deleteById(uuid);
        logger.info("User deleted: {}", uuid);
    }

    public void validateCredentials(String username, String password) {
        if (username == null || username.isBlank()) {
            throw new UserValidationException("Username must not be blank");
        }
        if (password == null || password.isBlank()) {
            throw new UserValidationException("Password must not be blank");
        }
    }
}