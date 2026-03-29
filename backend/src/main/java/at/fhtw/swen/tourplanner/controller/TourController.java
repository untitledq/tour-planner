package at.fhtw.swen.tourplanner.controller;

import at.fhtw.swen.tourplanner.controller.request.TourCreateRequest;
import at.fhtw.swen.tourplanner.persistence.entity.TourEntity;
import at.fhtw.swen.tourplanner.service.impl.TourService;
import at.fhtw.swen.tourplanner.security.JwtUserDetails;
import at.fhtw.swen.tourplanner.util.LoggerService;
import at.fhtw.swen.tourplanner.util.UUIDv7;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tours")
public class TourController {

    private final TourService service;
    private final Logger logger;

    public TourController(TourService service) {
        this.service = service;
        LoggerService logService = new LoggerService();
        this.logger = logService.getInstance().getLogger(getClass());
    }

    // Create a new tour
    @PostMapping
    public TourEntity create(
            @RequestBody TourCreateRequest req,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) {
        if (userDetails == null) {
            logger.warn("Create Tour: userDetails is null! JWT not parsed?");
            throw new RuntimeException("Unauthorized: user not authenticated");
        }

        UUID userId = userDetails.getUserId();
        logger.info("Create Tour called by user: {}", userId);
        logger.debug("Create Tour payload: {}", req);

        TourEntity tour = new TourEntity(
                UUIDv7.randomUUID(),
                userId,
                req.name(),
                req.description(),
                req.startName(),
                req.endName()
        );

        return service.save(tour);
    }

    // Get all tours for authenticated user
    @GetMapping
    public List<TourEntity> all(@AuthenticationPrincipal JwtUserDetails userDetails) {
        if (userDetails == null) {
            logger.warn("Get All Tours: userDetails is null!");
            throw new RuntimeException("Unauthorized: user not authenticated");
        }

        UUID userId = userDetails.getUserId();
        logger.info("Get All Tours called by user: {}", userId);
        return service.findAllByUser(userId);
    }

    // Get a single tour by ID for authenticated user
    @GetMapping("/{id}")
    public TourEntity getById(
            @PathVariable UUID id,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) {
        if (userDetails == null) {
            logger.warn("Get Tour {}: userDetails is null!", id);
            throw new RuntimeException("Unauthorized: user not authenticated");
        }

        UUID userId = userDetails.getUserId();
        logger.info("Get Tour {} called by user {}", id, userId);

        return service.findByIdAndUser(id, userId)
                .orElseThrow(() -> new RuntimeException("Tour not found or access denied"));
    }

    // Update a tour by ID for authenticated user
    @PutMapping("/{id}")
    public TourEntity update(
            @PathVariable UUID id,
            @RequestBody TourCreateRequest req,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) {
        if (userDetails == null) {
            logger.warn("Update Tour {}: userDetails is null!", id);
            throw new RuntimeException("Unauthorized: user not authenticated");
        }

        UUID userId = userDetails.getUserId();
        logger.info("Update Tour {} called by user {}", id, userId);
        logger.debug("Update Tour payload: {}", req);

        return service.updateTour(id, userId, req);
    }

    // Delete a tour by ID for authenticated user
    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) {
        if (userDetails == null) {
            logger.warn("Delete Tour {}: userDetails is null!", id);
            throw new RuntimeException("Unauthorized: user not authenticated");
        }

        UUID userId = userDetails.getUserId();
        logger.info("Delete Tour {} called by user {}", id, userId);
        service.deleteByUser(id, userId);
    }
}