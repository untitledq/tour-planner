package at.fhtw.swen.tourplanner.persistence.repository;

import at.fhtw.swen.tourplanner.persistence.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataSessionJpaRepository extends JpaRepository<Session, UUID> {
    Optional<Session> findByJti(UUID jti);
    Optional<Session> findByToken(String token);
    void deleteByExpiresAtBefore(Instant now);
    void deleteByRevokedTrueOrExpiresAtBefore(Instant now);
}
