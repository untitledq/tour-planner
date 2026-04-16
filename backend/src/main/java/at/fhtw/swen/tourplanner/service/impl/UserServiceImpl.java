package at.fhtw.swen.tourplanner.service.impl;

import at.fhtw.swen.tourplanner.persistence.port.PasswordHasher;
import at.fhtw.swen.tourplanner.persistence.port.UserRepository;
import at.fhtw.swen.tourplanner.service.UserService;
import at.fhtw.swen.tourplanner.service.dto.UserDto;
import at.fhtw.swen.tourplanner.util.LoggerService;
import at.fhtw.swen.tourplanner.util.UUIDv7;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final Logger logger;
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordHasher passwordHasher
    ) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.logger = LoggerService.getInstance().getLogger(getClass());
    }

    @Override
    public UserDto register(String username, String password, String email) {
        logger.info("Register attempt for username={}", username);

        validateCredentials(username, password);

        if (userRepository.existsByUsername(username)) {
            logger.warn("Registration failed: username already exists: {}", username);
            throw new IllegalArgumentException("username already exists");
        }

        String passwordHash = passwordHasher.hash(password);

        UserDto userDto = new UserDto(
                UUIDv7.randomUUID(),
                username,
                passwordHash,
                email,
                Instant.now()
        );

        UserDto saved = userRepository.save(userDto);

        logger.info("User registered successfully: username={}, id={}", saved.username(), saved.id());
        return saved;
    }

    @Override
    public void deleteUser(UUID uuid) {
        if (!userRepository.existsById(uuid)) {
            throw new IllegalArgumentException("User not found");
        }

        userRepository.deleteById(uuid);

        logger.info("User deleted: {}", uuid);
    }
}