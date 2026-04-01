package at.fhtw.swen.tourplanner.persistence.repository;

import at.fhtw.swen.tourplanner.persistence.entity.UserFavoriteTour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface UserFavoriteTourJpaRepository
        extends JpaRepository<UserFavoriteTour, Long> {

    boolean existsByUserIdAndTourId(UUID userId, UUID tourId);

    @Modifying
    @Transactional
    void deleteByUserIdAndTourId(UUID userId, UUID tourId);
}