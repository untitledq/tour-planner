package at.fhtw.swen.tourplanner.domain.model;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class Session {
    private UUID jti;
    private UUID userId;
    private String token;
    private Instant expiresAt;
    private Instant issuedAt;
    private boolean revoked;

    public Session(UUID jti, UUID userId, String token, Instant issuedAt, Instant expiresAt, boolean revoked) {
        this.jti = jti;
        this.userId = userId;
        this.token = token;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
    }

}
