package at.fhtw.swen.tourplanner.controller;

import at.fhtw.swen.tourplanner.service.dto.RouteDto;
import at.fhtw.swen.tourplanner.persistence.entity.Route;
import at.fhtw.swen.tourplanner.persistence.entity.Tour;
import at.fhtw.swen.tourplanner.persistence.repository.RouteJpaRepository;
import at.fhtw.swen.tourplanner.persistence.repository.TourJpaRepository;
import at.fhtw.swen.tourplanner.security.JwtUserDetails;
import at.fhtw.swen.tourplanner.util.LoggerService;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/tours") // changed from /api/routes
public class RouteController {

    private final RouteJpaRepository routeRepository;
    private final TourJpaRepository tourRepository;
    private final Logger logger = LoggerService.getInstance().getLogger(getClass());

    public RouteController(RouteJpaRepository routeRepository,
                           TourJpaRepository tourRepository) {
        this.routeRepository = routeRepository;
        this.tourRepository = tourRepository;
    }

    @GetMapping("/{tourId}/route") // changed mapping to match OpenAPI
    public ResponseEntity<RouteDto> getRouteForTour(
            @PathVariable UUID tourId,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) {
        UUID userId = userDetails.getUserId();
        logger.info("GET /api/tours/{}/route called by user {}", tourId, userId);

        // Step 1: Fetch tour and check ownership
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> {
                    logger.warn("Tour {} not found", tourId);
                    return new RuntimeException("Tour not found");
                });

        logger.info("Fetched tour {} created by {}", tour.getId(), tour.getCreatedBy());

        if (!tour.getCreatedBy().equals(userId)) {
            logger.warn("Access denied: user {} does not own tour {}", userId, tourId);
            return ResponseEntity.status(403).build();
        }

        // Step 2: Fetch route for tour
        Route route = routeRepository.findByTourId(tour.getId())
                .orElseThrow(() -> {
                    logger.warn("Route not found for tour {}", tour.getId());
                    return new RuntimeException("Route not found");
                });

        logger.info("Fetched route {} for tour {}", route.getId(), tour.getId());

        // Step 3: Build response
        RouteDto response = new RouteDto(
                route.getDistance(),
                route.getDuration(),
                route.getCoordinates()
        );

        logger.info("Returning route response for tour {}", tour.getId());
        return ResponseEntity.ok(response);
    }
}