package at.fhtw.swen.tourplanner.service.impl;

import at.fhtw.swen.tourplanner.service.dto.UserDto;
import at.fhtw.swen.tourplanner.persistence.port.PasswordHasher;
import at.fhtw.swen.tourplanner.persistence.port.UserRepository;
import at.fhtw.swen.tourplanner.util.UUIDv7;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements at.fhtw.swen.tourplanner.service.UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public UserServiceImpl(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public UserDto register(String username, String password, String email) {
        log.info("Register attempt for username={}", username);

        validateCredentials(username, password);

        if (userRepository.existsByUsername(username)) {
            log.warn("Registration failed: username already exists: {}", username);
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
        log.info("UserDto registered successfully: username={}, id={}", saved.username(), saved.id());

        return saved;
    }

    @Override
    public String login(String username, String rawPassword) {
        log.info("Login attempt for username={}", username);

        validateCredentials(username, rawPassword);

        Optional<UserDto> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            log.warn("Login failed: username not found: {}", username);
            throw new SecurityException("Username not found");
        }

        UserDto u = user.get();

        if (passwordHasher.invalid(rawPassword, u.passwordHash())) {
            log.warn("Login failed: invalid password for username={}", username);
            throw new SecurityException("Invalid credentials");
        }

        log.info("Login successful for username={}", username);
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