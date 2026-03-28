package at.fhtw.swen.tourplanner.persistence.adapter;

import at.fhtw.swen.tourplanner.persistence.repository.SpringDataSessionJpaRepository;
import at.fhtw.swen.tourplanner.service.dto.Session;
import at.fhtw.swen.tourplanner.persistence.port.SessionRepository;
import at.fhtw.swen.tourplanner.persistence.entity.SessionEntity;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaSessionRepositoryAdapter implements SessionRepository {

    private final SpringDataSessionJpaRepository repository;

    public JpaSessionRepositoryAdapter(SpringDataSessionJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Session session){
        SessionEntity saved = repository.save(toEntity(session));
        toDomain(saved);
    }

    @Override
    public Optional<Session> findByJti(UUID jti) {
        return repository.findByJti(jti).map(this::toDomain);
    }

    @Override
    public void revokeByJti(UUID jti) {
        repository.findById(jti).ifPresent(entity -> repository.save(revoke(entity)));
    }

    @Override
    public void deleteRevokedOrExpired(Instant now) {
        repository.deleteByRevokedTrueOrExpiresAtBefore(now);
    }

    private SessionEntity toEntity(Session session) {
        return new SessionEntity(
                session.getJti(),
                session.getUserId(),
                session.getToken(),
                session.getIssuedAt(),
                session.getExpiresAt(),
                session.isRevoked()
        );
    }

    private Session toDomain(SessionEntity entity) {
        return new Session(
                entity.getJti(),
                entity.getUserId(),
                entity.getToken(),
                entity.getIssuedAt(),
                entity.getExpiresAt(),
                entity.isRevoked()
        );
    }

    private SessionEntity revoke(SessionEntity entity) {
        return new SessionEntity(
                entity.getJti(),
                entity.getUserId(),
                entity.getToken(),
                entity.getIssuedAt(),
                entity.getExpiresAt(),
                true
        );
    }
}
