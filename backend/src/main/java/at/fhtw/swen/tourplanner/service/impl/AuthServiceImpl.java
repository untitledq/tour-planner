package at.fhtw.swen.tourplanner.service.impl;

import at.fhtw.swen.tourplanner.service.dto.SessionDto;
import at.fhtw.swen.tourplanner.service.dto.UserDto;
import at.fhtw.swen.tourplanner.persistence.port.PasswordHasher;
import at.fhtw.swen.tourplanner.persistence.port.SessionRepository;
import at.fhtw.swen.tourplanner.persistence.port.TokenPayload;
import at.fhtw.swen.tourplanner.persistence.port.TokenService;
import at.fhtw.swen.tourplanner.persistence.port.UserRepository;
import at.fhtw.swen.tourplanner.controller.response.LoginResponse;
import at.fhtw.swen.tourplanner.util.UUIDv7;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class AuthServiceImpl implements at.fhtw.swen.tourplanner.service.AuthService {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final TokenService tokenService;
    private final SessionRepository sessionRepository;
    private final long expirationMinutes;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            TokenService tokenService,
            SessionRepository sessionRepository,
            @Value("${security.jwt.expiration-minutes}") long expirationMinutes
    ) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.tokenService = tokenService;
        this.sessionRepository = sessionRepository;
        this.expirationMinutes = expirationMinutes;
    }

    @Override
    public LoginResponse login(String username, String rawPassword) {
        UserDto userDto = userRepository.findByUsername(username)
                .orElseThrow(() -> new SecurityException("Invalid credentials"));

        if (passwordHasher.invalid(rawPassword, userDto.passwordHash())) {
            throw new SecurityException("Invalid credentials");
        }

        UUID sessionId = UUIDv7.randomUUID();
        Instant expiresAt = Instant.now().plus(expirationMinutes, ChronoUnit.MINUTES);

        String token = tokenService.createToken(
                userDto.id(),
                userDto.username(),
                sessionId,
                expiresAt
        );

        SessionDto sessionDto = new SessionDto(
                sessionId,
                userDto.id(),
                token,
                Instant.now(),
                expiresAt,
                false
        );

        sessionRepository.save(sessionDto);

        return new LoginResponse(token, expiresAt, sessionId);
    }

    @Override
    public void logout(String token) {
        if (token == null || token.isBlank()) {
            throw new SecurityException("Missing token");
        }

        TokenPayload payload = tokenService.parseAndValidate(token);
        sessionRepository.revokeByJti(payload.sessionId());
    }

    @Override
    public TokenPayload parseToken(String token) {
        return tokenService.parseAndValidate(token);
    }
}
