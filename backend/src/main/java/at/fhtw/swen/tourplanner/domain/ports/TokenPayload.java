package at.fhtw.swen.tourplanner.domain.ports;

import java.time.Instant;
import java.util.UUID;

public record TokenPayload(
        UUID userId,
        String username,
        UUID sessionId,
        Instant expiresAt
) {
}