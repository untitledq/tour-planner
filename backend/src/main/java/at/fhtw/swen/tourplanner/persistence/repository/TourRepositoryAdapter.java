package at.fhtw.swen.tourplanner.persistence.repository;

import at.fhtw.swen.tourplanner.persistence.entity.Tour;
import at.fhtw.swen.tourplanner.persistence.exceptions.TourAlreadyExistsException;
import at.fhtw.swen.tourplanner.persistence.exceptions.TourNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class TourRepositoryAdapter {

    private final TourRepository tourRepository;

    public TourRepositoryAdapter(TourRepository tourRepository) {
        this.tourRepository = tourRepository;
    }

    public List<Tour> findAllByCreatedBy(UUID createdBy) {
        return tourRepository.findAllByCreatedBy(createdBy);
    }

    public Tour findByIdAndCreatedBy(UUID id, UUID createdBy) {
        return tourRepository.findByIdAndCreatedBy(id, createdBy)
                .orElseThrow(() ->
                        new TourNotFoundException(
                                "Tour with id " + id + " not found for user " + createdBy));
    }

    public void deleteByIdAndCreatedBy(UUID id, UUID createdBy) {
        Tour tour = findByIdAndCreatedBy(id, createdBy);
        tourRepository.delete(tour);
    }

    public Tour save(Tour tour) {
        if (tour.getId() != null && tourRepository.existsById(tour.getId())) {
            throw new TourAlreadyExistsException(
                    "Tour with id " + tour.getId() + " already exists");
        }

        return tourRepository.save(tour);
    }

    public List<Tour> findAllCreatedAndFavoritesBy(UUID userId) {
        return tourRepository.findAllCreatedAndFavoritesBy(userId);
    }

    public List<Tour> findAllCreatedButNotFavoritesBy(UUID userId) {
        return tourRepository.findAllCreatedButNotFavoritesBy(userId);
    }
}