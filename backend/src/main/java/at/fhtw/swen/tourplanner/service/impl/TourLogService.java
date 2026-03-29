package at.fhtw.swen.tourplanner.service.impl;

import at.fhtw.swen.tourplanner.controller.request.TourLogCreateRequest;
import at.fhtw.swen.tourplanner.controller.request.TourLogUpdateRequest;
import at.fhtw.swen.tourplanner.controller.response.TourLogResponse;
import at.fhtw.swen.tourplanner.persistence.entity.TourLogEntity;
import at.fhtw.swen.tourplanner.persistence.repository.TourLogJpaRepository;
import at.fhtw.swen.tourplanner.util.UUIDv7;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TourLogService {

    private final TourLogJpaRepository repository;

    public TourLogService(TourLogJpaRepository repository) {
        this.repository = repository;
    }

    // Entity -> Response
    private TourLogResponse toResponse(TourLogEntity e) {
        return new TourLogResponse(
                e.getId(),
                e.getTourId(),
                e.getDateTime(),
                e.getRating(),
                e.getComment()
        );
    }

    // Get all logs for user
    public List<TourLogResponse> findAllByUser(UUID userId) {
        return repository.findAllByCreatedBy(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Get single log for user
    public TourLogResponse findByIdAndUser(UUID id, UUID userId) {
        TourLogEntity entity = repository.findByIdAndCreatedBy(id, userId)
                .orElseThrow(() -> new RuntimeException("Tour Log not found or access denied"));

        return toResponse(entity);
    }

    // Create new log (ownership is set here!)
    public TourLogResponse create(TourLogCreateRequest req, UUID userId) {
        TourLogEntity entity = new TourLogEntity(
                UUIDv7.randomUUID(),
                req.tourId(),
                userId,
                req.dateTime(),
                req.rating(),
                req.comment()
        );

        return toResponse(repository.save(entity));
    }

    // Update existing log (only if owned by user)
    @Transactional
    public TourLogResponse update(UUID id, TourLogUpdateRequest req, UUID userId) {
        TourLogEntity entity = repository.findByIdAndCreatedBy(id, userId)
                .orElseThrow(() -> new RuntimeException("Tour Log not found or access denied"));

        entity.setDateTime(req.dateTime());
        entity.setRating(req.rating());
        entity.setComment(req.comment());

        return toResponse(entity);
    }

    // Delete only if owned by user
    @Transactional
    public void deleteByUser(UUID id, UUID userId) {
        TourLogEntity entity = repository.findByIdAndCreatedBy(id, userId)
                .orElseThrow(() -> new RuntimeException("Tour Log not found or access denied"));

        repository.delete(entity);
    }
}