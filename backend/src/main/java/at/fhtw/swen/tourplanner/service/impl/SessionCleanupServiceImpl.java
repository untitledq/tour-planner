package at.fhtw.swen.tourplanner.service.impl;

import at.fhtw.swen.tourplanner.persistence.repository.adapter.SessionRepositoryAdapter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class SessionCleanupServiceImpl implements at.fhtw.swen.tourplanner.service.SessionCleanupService {

    private final SessionRepositoryAdapter sessionRepositoryAdapter;

    public SessionCleanupServiceImpl(SessionRepositoryAdapter sessionRepositoryAdapter) {
        this.sessionRepositoryAdapter = sessionRepositoryAdapter;
    }

    @Transactional
    @Scheduled(fixedDelayString = "${session.cleanup.fixed-delay-ms:900000}")
    @Override
    public void cleanupRevokedAndExpiredSessions() {
        sessionRepositoryAdapter.deleteExpiredSessions(Instant.now());
    }
}