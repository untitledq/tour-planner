package at.fhtw.swen.tourplanner.controller;

import at.fhtw.swen.tourplanner.persistence.entity.UserFavoriteTour;
import at.fhtw.swen.tourplanner.persistence.repository.TourJpaRepository;
import at.fhtw.swen.tourplanner.persistence.repository.UserFavoriteTourJpaRepository;
import at.fhtw.swen.tourplanner.security.JwtUserDetails;
import at.fhtw.swen.tourplanner.util.LoggerService;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users/me/favorites")
public class FavoriteController {

    private final UserFavoriteTourJpaRepository favoriteRepository;
    private final TourJpaRepository tourRepository;
    private final Logger logger = LoggerService.getInstance().getLogger(getClass());

    public FavoriteController(UserFavoriteTourJpaRepository favoriteRepository,
                              TourJpaRepository tourRepository) {
        this.favoriteRepository = favoriteRepository;
        this.tourRepository = tourRepository;
    }

    // Add favorite
    @PostMapping("/{tourId}")
    public ResponseEntity<Void> addFavorite(
            @PathVariable UUID tourId,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) {
        UUID userId = userDetails.getUserId();
        logger.info("Add favorite {} for user {}", tourId, userId);

        if (!tourRepository.existsById(tourId)) {
            throw new RuntimeException("Tour not found");
        }

        if (!favoriteRepository.existsByUserIdAndTourId(userId, tourId)) {
            favoriteRepository.save(new UserFavoriteTour(userId, tourId));
        }

        return ResponseEntity.noContent().build();
    }

    // Remove favorite
    @DeleteMapping("/{tourId}")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable UUID tourId,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) {
        UUID userId = userDetails.getUserId();
        logger.info("Remove favorite {} for user {}", tourId, userId);

        favoriteRepository.deleteByUserIdAndTourId(userId, tourId);

        return ResponseEntity.noContent().build();
    }
}