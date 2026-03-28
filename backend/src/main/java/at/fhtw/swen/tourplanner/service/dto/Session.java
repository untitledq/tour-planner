package at.fhtw.swen.tourplanner.service.dto;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class Session {
    private final UUID jti;
    private final UUID userId;
    private final String token;
    private final Instant expiresAt;
    private final Instant issuedAt;
    private final boolean revoked;

    public Session(UUID jti, UUID userId, String token, Instant issuedAt, Instant expiresAt, boolean revoked) {
        this.jti = jti;
        this.userId = userId;
        this.token = token;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
    }
}