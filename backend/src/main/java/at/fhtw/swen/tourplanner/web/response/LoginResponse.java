package at.fhtw.swen.tourplanner.web.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

public record LoginResponse(
        String token,
        @JsonProperty("expires_at") Instant expiresAt,
        @JsonProperty("session_id") UUID sessionId
) {
}