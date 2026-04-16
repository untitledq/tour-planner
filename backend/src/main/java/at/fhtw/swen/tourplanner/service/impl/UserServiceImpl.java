package at.fhtw.swen.tourplanner.service.impl;

import at.fhtw.swen.tourplanner.service.dto.UserDto;
import at.fhtw.swen.tourplanner.persistence.port.PasswordHasher;
import at.fhtw.swen.tourplanner.persistence.port.UserRepository;
import at.fhtw.swen.tourplanner.util.LoggerService;
import at.fhtw.swen.tourplanner.util.UUIDv7;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements at.fhtw.swen.tourplanner.service.UserService {

    private final Logger logger;

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public UserServiceImpl(UserRepository userRepository, PasswordHasher passwordHasher) {
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
        logger.info("UserDto registered successfully: username={}, id={}", saved.username(), saved.id());

        return saved;
    }

    @Override
    public String login(String username, String rawPassword) {
        logger.info("Login attempt for username={}", username);

        validateCredentials(username, rawPassword);

        Optional<UserDto> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            logger.warn("Login failed: username not found: {}", username);
            throw new SecurityException("Username not found");
        }

        UserDto u = user.get();

        if (passwordHasher.invalid(rawPassword, u.passwordHash())) {
            logger.warn("Login failed: invalid password for username={}", username);
            throw new SecurityException("Invalid credentials");
        }

        logger.info("Login successful for username={}", username);
        return u.username();
    }

    @Override
    public void deleteUser(UUID uuid) {
        if (!userRepository.existsById(uuid)) {
            throw new IllegalArgumentException("UserDto not found");
        }

        userRepository.deleteById(uuid);
    }
}