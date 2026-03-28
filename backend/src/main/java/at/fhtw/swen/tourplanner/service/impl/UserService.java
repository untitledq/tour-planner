package at.fhtw.swen.tourplanner.service.impl;

import at.fhtw.swen.tourplanner.__domain.model.User;
import at.fhtw.swen.tourplanner.__domain.ports.PasswordHasher;
import at.fhtw.swen.tourplanner.__domain.ports.UserRepository;
import at.fhtw.swen.tourplanner.util.UUIDv7;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements at.fhtw.swen.tourplanner.service.UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public UserService(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public User register(String username, String password, String email) {
        log.info("Register attempt for username={}", username);

        validateCredentials(username, password);

        if (userRepository.existsByUsername(username)) {
            log.warn("Registration failed: username already exists: {}", username);
            throw new IllegalArgumentException("username already exists");
        }

        String passwordHash = passwordHasher.hash(password);

        User user = new User(
                UUIDv7.randomUUID(),
                username,
                passwordHash,
                email,
                Instant.now()
        );

        User saved = userRepository.save(user);
        log.info("User registered successfully: username={}, id={}", saved.username(), saved.id());

        return saved;
    }

    @Override
    public String login(String username, String rawPassword) {
        log.info("Login attempt for username={}", username);

        validateCredentials(username, rawPassword);

        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            log.warn("Login failed: username not found: {}", username);
            throw new SecurityException("Username not found");
        }

        User u = user.get();

        if (!passwordHasher.matches(rawPassword, u.passwordHash())) {
            log.warn("Login failed: invalid password for username={}", username);
            throw new SecurityException("Invalid credentials");
        }

        log.info("Login successful for username={}", username);
        return u.username();
    }

    @Override
    public void deleteUser(UUID uuid) {
        if (!userRepository.existsById(uuid)) {
            throw new IllegalArgumentException("User not found");
        }

        userRepository.deleteById(uuid);
    }
}