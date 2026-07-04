package at.fhtw.swen.tourplanner.persistence.repository.adapter;

import at.fhtw.swen.tourplanner.persistence.entity.TourLog;
import at.fhtw.swen.tourplanner.controller.exception.TourLogNotFoundException;
import at.fhtw.swen.tourplanner.persistence.repository.TourLogRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class TourLogRepositoryAdapter {

    private final TourLogRepository tourLogRepository;

    public TourLogRepositoryAdapter(TourLogRepository tourLogRepository) {
        this.tourLogRepository = tourLogRepository;
    }

    public List<TourLog> findByTourId(UUID tourId) {
        return tourLogRepository.findByTourId(tourId);
    }

    public List<TourLog> findAllByCreatedBy(UUID userId) {
        return tourLogRepository.findAllByCreatedBy(userId);
    }

    public TourLog findByIdAndCreatedBy(UUID id, UUID userId) {
        return tourLogRepository.findByIdAndCreatedBy(id, userId)
                .orElseThrow(() ->
                        new TourLogNotFoundException(
                                "Tour log with id " + id + " not found for user " + userId));
    }

    public List<TourLog> findAllByTourIdAndCreatedBy(UUID tourId, UUID createdBy) {
        return tourLogRepository.findAllByTourIdAndCreatedBy(tourId, createdBy);
    }

    public void deleteByIdAndCreatedBy(UUID id, UUID userId) {
        TourLog tourLog = findByIdAndCreatedBy(id, userId);
        tourLogRepository.delete(tourLog);
    }

    public TourLog save(TourLog tourLog) {
        return tourLogRepository.save(tourLog);
    }

    public TourLog findById(UUID id) {
        return tourLogRepository.findById(id)
                .orElseThrow(() ->
                        new TourLogNotFoundException(
                                "Tour log with id " + id + " not found"));
    }
}