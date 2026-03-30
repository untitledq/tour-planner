package at.fhtw.swen.tourplanner.persistence.port;

import at.fhtw.swen.tourplanner.service.dto.SessionDto;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface SessionRepository {
    void save(SessionDto sessionDto);
    Optional<SessionDto> findByJti(UUID jti);
    void revokeByJti(UUID jti);
    void deleteRevokedOrExpired(Instant now);
}
