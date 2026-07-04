package at.fhtw.swen.tourplanner.persistence.repository.adapter;

import at.fhtw.swen.tourplanner.persistence.entity.Session;
import at.fhtw.swen.tourplanner.controller.exception.SessionAlreadyExistsException;
import at.fhtw.swen.tourplanner.controller.exception.SessionNotFoundException;
import at.fhtw.swen.tourplanner.persistence.repository.SessionRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public class SessionRepositoryAdapter {

    private final SessionRepository sessionRepository;

    public SessionRepositoryAdapter(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public Session findById(UUID id) {
        return sessionRepository.findById(id)
                .orElseThrow(() ->
                        new SessionNotFoundException(
                                "Session with id " + id + " not found"));
    }

    public Session save(Session session) {
        if (sessionRepository.existsByJti(session.getJti())) {
            throw new SessionAlreadyExistsException(
                    "Session with JTI " + session.getJti() + " already exists");
        }

        return sessionRepository.save(session);
    }

    public void revokeByJti(UUID jti) {
        if (!sessionRepository.existsByJti(jti)) {
            throw new SessionNotFoundException(
                    "Session with JTI " + jti + " not found");
        }

        sessionRepository.revokeByJti(jti);
    }

    public void deleteExpiredSessions(Instant now) {
        sessionRepository.deleteByRevokedTrueOrExpiresAtBefore(now);
    }
}