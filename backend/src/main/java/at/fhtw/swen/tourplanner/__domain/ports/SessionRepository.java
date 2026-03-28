package at.fhtw.swen.tourplanner.__domain.ports;

import at.fhtw.swen.tourplanner.__domain.model.Session;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface SessionRepository {
    void save(Session session);
    Optional<Session> findByJti(UUID jti);
    void revokeByJti(UUID jti);
    void deleteRevokedOrExpired(Instant now);
}
