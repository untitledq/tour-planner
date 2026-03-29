package at.fhtw.swen.tourplanner.service.impl;

import at.fhtw.swen.tourplanner.persistence.entity.TourLogEntity;
import at.fhtw.swen.tourplanner.persistence.repository.TourLogJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TourLogService {

    private final TourLogJpaRepository repository;

    public TourLogService(TourLogJpaRepository repository) {
        this.repository = repository;
    }

    // Get all tour logs (for admin/testing, not user-specific)
    public List<TourLogEntity> findAll() {
        return repository.findAll();
    }

    // Get all tour logs for a specific user
    public List<TourLogEntity> findAllByUser(UUID userId) {
        return repository.findAllByCreatedBy(userId);
    }

    // Get a single tour log by ID for a specific user
    public Optional<TourLogEntity> findByIdAndUser(UUID id, UUID userId) {
        return repository.findByIdAndCreatedBy(id, userId);
    }

    // Save a new or updated tour log
    public TourLogEntity save(TourLogEntity tourLog) {
        return repository.save(tourLog);
    }

    // Delete a tour log by ID (general, not user-specific)
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    // Delete a tour log by ID only if it belongs to the user
    @Transactional
    public void deleteByUser(UUID id, UUID userId) {
        if (!repository.findByIdAndCreatedBy(id, userId).isPresent()) {
            throw new RuntimeException("Tour Log not found or access denied");
        }
        repository.deleteByIdAndCreatedBy(id, userId);
    }
}