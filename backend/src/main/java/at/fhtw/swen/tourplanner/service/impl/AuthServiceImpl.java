package at.fhtw.swen.tourplanner.service.impl;

import at.fhtw.swen.tourplanner.persistence.entity.Session;
import at.fhtw.swen.tourplanner.persistence.entity.User;
import at.fhtw.swen.tourplanner.controller.exception.UserNotFoundException;
import at.fhtw.swen.tourplanner.persistence.repository.adapter.SessionRepositoryAdapter;
import at.fhtw.swen.tourplanner.persistence.repository.adapter.UserRepositoryAdapter;
import at.fhtw.swen.tourplanner.security.core.PasswordHasher;
import at.fhtw.swen.tourplanner.security.core.TokenPayload;
import at.fhtw.swen.tourplanner.security.core.TokenService;
import at.fhtw.swen.tourplanner.service.AuthService;
import at.fhtw.swen.tourplanner.controller.response.LoginResponse;
import at.fhtw.swen.tourplanner.controller.exception.UserAuthenticationException;
import at.fhtw.swen.tourplanner.util.UUIDv7;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final PasswordHasher passwordHasher;
    private final TokenService tokenService;
    private final SessionRepositoryAdapter sessionRepositoryAdapter;
    private final UserRepositoryAdapter userRepositoryAdapter;
    private final long expirationMinutes;

    public AuthServiceImpl(
            PasswordHasher passwordHasher,
            TokenService tokenService,
            SessionRepositoryAdapter sessionRepositoryAdapter,
            UserRepositoryAdapter userRepositoryAdapter,
            @Value("${security.jwt.expiration-minutes}") long expirationMinutes
    ) {
        this.passwordHasher = passwordHasher;
        this.tokenService = tokenService;
        this.sessionRepositoryAdapter = sessionRepositoryAdapter;
        this.userRepositoryAdapter = userRepositoryAdapter;
        this.expirationMinutes = expirationMinutes;
    }

    @Override
    public LoginResponse login(String username, String rawPassword) {
        if (username == null || username.isBlank() ||
                rawPassword == null || rawPassword.isBlank()) {
            throw new UserAuthenticationException("Invalid credentials");
        }

        User user;
        try {
            user = userRepositoryAdapter.findByUsername(username)
                    .orElseThrow(() -> new UserAuthenticationException("Invalid credentials"));
        } catch (UserNotFoundException e) {
            throw new UserAuthenticationException("Invalid credentials");
        }

        if (passwordHasher.invalid(rawPassword, user.getPasswordHash())) {
            throw new UserAuthenticationException("Invalid credentials");
        }

        UUID sessionId = UUIDv7.randomUUID();
        Instant expiresAt = Instant.now().plus(expirationMinutes, ChronoUnit.MINUTES);

        String token = tokenService.createToken(
                user.getId(),
                user.getUsername(),
                sessionId,
                expiresAt
        );

        Session session = new Session(
                sessionId,
                user.getId(),
                token,
                Instant.now(),
                expiresAt,
                false
        );

        sessionRepositoryAdapter.save(session);

        return new LoginResponse(token, expiresAt, sessionId);
    }

    @Override
    @Transactional
    public void logout(String token) {
        if (token == null || token.isBlank()) {
            throw new SecurityException("Missing token");
        }

        TokenPayload payload = tokenService.parseAndValidate(token);
        sessionRepositoryAdapter.revokeByJti(payload.sessionId());
    }

    @Override
    public TokenPayload parseToken(String token) {
        return tokenService.parseAndValidate(token);
    }
}