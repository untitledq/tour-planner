package at.fhtw.swen.tourplanner.persistence.repository;

import at.fhtw.swen.tourplanner.persistence.entity.TourEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TourJpaRepository extends JpaRepository<TourEntity, UUID> {

    // find all tours created by a user
    List<TourEntity> findAllByCreatedBy(UUID createdBy);

    // find a specific tour by id and user
    Optional<TourEntity> findByIdAndCreatedBy(UUID id, UUID createdBy);

    // delete a tour by id and user
    void deleteByIdAndCreatedBy(UUID id, UUID createdBy);
}