package at.fhtw.swen.tourplanner.service.impl;

import at.fhtw.swen.tourplanner.persistence.repository.SessionJpaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class SessionCleanupServiceImpl implements at.fhtw.swen.tourplanner.service.SessionCleanupService {

    private final SessionJpaRepository sessionJpaRepository;

    public SessionCleanupServiceImpl(SessionJpaRepository sessionJpaRepository) {
        this.sessionJpaRepository = sessionJpaRepository;
    }

    @Transactional
    @Scheduled(fixedDelayString = "${session.cleanup.fixed-delay-ms:900000}")
    @Override
    public void cleanupRevokedAndExpiredSessions() {
        sessionJpaRepository.deleteByRevokedTrueOrExpiresAtBefore(Instant.now());
    }
}