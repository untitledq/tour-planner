package at.fhtw.swen.tourplanner.service.impl;

import at.fhtw.swen.tourplanner.controller.request.TourCreateRequest;
import at.fhtw.swen.tourplanner.persistence.entity.TourEntity;
import at.fhtw.swen.tourplanner.persistence.repository.TourJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TourService {

    private final TourJpaRepository repository;

    public TourService(TourJpaRepository repository) {
        this.repository = repository;
    }

    public TourEntity save(TourEntity tour) {
        return repository.save(tour);
    }

    public List<TourEntity> findAllByUser(UUID userId) {
        return repository.findAllByCreatedBy(userId);
    }

    public Optional<TourEntity> findByIdAndUser(UUID id, UUID userId) {
        return repository.findByIdAndCreatedBy(id, userId);
    }

    public TourEntity updateTour(UUID id, UUID userId, TourCreateRequest req) {
        return repository.findByIdAndCreatedBy(id, userId)
                .map(tour -> {
                    tour = new TourEntity(
                            tour.getId(),
                            tour.getCreatedBy(),
                            req.name(),
                            req.description(),
                            req.startName(),
                            req.endName()
                    );
                    return repository.save(tour);
                })
                .orElseThrow(() -> new RuntimeException("Tour not found or access denied"));
    }

    @Transactional
    public void deleteByUser(UUID id, UUID userId) {
        if (!repository.findByIdAndCreatedBy(id, userId).isPresent()) {
            throw new RuntimeException("Tour not found or access denied");
        }
        repository.deleteByIdAndCreatedBy(id, userId);
    }
}