package at.fhtw.swen.tourplanner.__domain.ports;

import java.time.Instant;
import java.util.UUID;

public interface TokenService {
    String createToken(UUID userId, String username, UUID sessionId, Instant expiresAt);
    TokenPayload parseAndValidate(String token);
}
