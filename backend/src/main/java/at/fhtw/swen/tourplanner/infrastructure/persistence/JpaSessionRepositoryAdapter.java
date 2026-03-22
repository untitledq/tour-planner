package at.fhtw.swen.tourplanner.infrastructure.persistence;

import at.fhtw.swen.tourplanner.domain.model.Session;
import at.fhtw.swen.tourplanner.domain.ports.SessionRepository;
import at.fhtw.swen.tourplanner.infrastructure.persistence.entity.SessionEntity;
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
    public Session save(Session session){
        SessionEntity saved = repository.save(toEntity(session));
        return toDomain(saved);
    }

    @Override
    public Optional<Session> findByJti(UUID jti) {
        return repository.findByJti(jti).map(this::toDomain);
    }

    @Override
    public Optional<Session> findByToken(String token) {
        return repository.findByToken(token).map(this::toDomain);
    }

    @Override
    public void revokeByJti(UUID jti) {
        repository.findById(jti).ifPresent(entity -> {
            repository.save(revoke(entity));
        });
    }

    @Override
    public void revokeByToken(String token) {
        repository.findByToken(token).ifPresent(entity -> {
            repository.save(revoke(entity));
        });
    }

//    @Override
//    public void revokeAllActiveByUserId(UUID userId) {
//        repository.findAll().stream()
//                .filter(session -> session.getUserId().equals(userId) && !session.isRevoked())
//                .forEach(session -> repository.save(new SessionEntity(
//                        session.getJti(),
//                        session.getUserId(),
//                        session.getToken(),
//                        session.getIssuedAt(),
//                        session.getExpiresAt(),
//                        true
//                )));
//    }

    @Override
    public void deleteExpired(Instant now) {
        repository.deleteByExpiresAtBefore(now);
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
