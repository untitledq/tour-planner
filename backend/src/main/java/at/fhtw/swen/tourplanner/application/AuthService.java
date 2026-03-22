package at.fhtw.swen.tourplanner.application;

import at.fhtw.swen.tourplanner.domain.model.Session;
import at.fhtw.swen.tourplanner.domain.model.User;
import at.fhtw.swen.tourplanner.domain.ports.PasswordHasher;
import at.fhtw.swen.tourplanner.domain.ports.SessionRepository;
import at.fhtw.swen.tourplanner.domain.ports.TokenPayload;
import at.fhtw.swen.tourplanner.domain.ports.TokenService;
import at.fhtw.swen.tourplanner.domain.ports.UserRepository;
import at.fhtw.swen.tourplanner.web.response.LoginResponse;
import at.fhtw.swen.tourplanner.util.UUIDv7;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final TokenService tokenService;
    private final SessionRepository sessionRepository;
    private final long expirationMinutes;

    public AuthService(
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

    public LoginResponse login(String username, String rawPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new SecurityException("Invalid credentials"));

        if (!passwordHasher.matches(rawPassword, user.getPassword())) {
            throw new SecurityException("Invalid credentials");
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

        sessionRepository.save(session);

        return new LoginResponse(token, expiresAt, sessionId);
    }

    public void logout(String token) {
        if (token == null || token.isBlank()) {
            return;
        }

        try {
            TokenPayload payload = tokenService.parseAndValidate(token);
            sessionRepository.revokeByJti(payload.sessionId());
        } catch (Exception ignored) {
            sessionRepository.revokeByToken(token);
        }
    }
}
