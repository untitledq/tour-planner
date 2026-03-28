package at.fhtw.swen.tourplanner.persistence.port;

import java.time.Instant;
import java.util.UUID;

public interface TokenService {
    String createToken(UUID userId, String username, UUID sessionId, Instant expiresAt);
    TokenPayload parseAndValidate(String token);
}
