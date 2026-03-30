package at.fhtw.swen.tourplanner.persistence.adapter;

import at.fhtw.swen.tourplanner.persistence.repository.SpringDataSessionJpaRepository;
import at.fhtw.swen.tourplanner.service.dto.SessionDto;
import at.fhtw.swen.tourplanner.persistence.port.SessionRepository;
import at.fhtw.swen.tourplanner.persistence.entity.Session;
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
    public void save(SessionDto sessionDto){
        Session saved = repository.save(toEntity(sessionDto));
        toDomain(saved);
    }

    @Override
    public Optional<SessionDto> findByJti(UUID jti) {
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

    private Session toEntity(SessionDto sessionDto) {
        return new Session(
                sessionDto.getJti(),
                sessionDto.getUserId(),
                sessionDto.getToken(),
                sessionDto.getIssuedAt(),
                sessionDto.getExpiresAt(),
                sessionDto.isRevoked()
        );
    }

    private SessionDto toDomain(Session entity) {
        return new SessionDto(
                entity.getJti(),
                entity.getUserId(),
                entity.getToken(),
                entity.getIssuedAt(),
                entity.getExpiresAt(),
                entity.isRevoked()
        );
    }

    private Session revoke(Session entity) {
        return new Session(
                entity.getJti(),
                entity.getUserId(),
                entity.getToken(),
                entity.getIssuedAt(),
                entity.getExpiresAt(),
                true
        );
    }
}
