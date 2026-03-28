package at.fhtw.swen.tourplanner.service.impl;

import at.fhtw.swen.tourplanner.__domain.ports.SessionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class SessionCleanupService implements at.fhtw.swen.tourplanner.service.SessionCleanupService {

    private final SessionRepository sessionRepository;

    public SessionCleanupService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Transactional
    @Scheduled(fixedDelayString = "${session.cleanup.fixed-delay-ms:900000}")
    @Override
    public void cleanupRevokedAndExpiredSessions() {
        sessionRepository.deleteRevokedOrExpired(Instant.now());
    }
}