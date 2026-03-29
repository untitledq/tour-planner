package at.fhtw.swen.tourplanner.controller;

import at.fhtw.swen.tourplanner.controller.request.TourLogCreateRequest;
import at.fhtw.swen.tourplanner.persistence.entity.TourLogEntity;
import at.fhtw.swen.tourplanner.service.impl.TourLogService;
import at.fhtw.swen.tourplanner.security.JwtUserDetails;
import at.fhtw.swen.tourplanner.util.LoggerService;
import at.fhtw.swen.tourplanner.util.UUIDv7;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tour-logs")
public class TourLogController {

    private final TourLogService service;
    private final Logger logger;

    public TourLogController(TourLogService service) {
        this.service = service;
        LoggerService logService = new LoggerService();
        this.logger = logService.getInstance().getLogger(getClass());
    }

    // Get all tour logs for authenticated user
    @GetMapping
    public List<TourLogEntity> all(@AuthenticationPrincipal JwtUserDetails userDetails) {
        if (userDetails == null) {
            logger.warn("Get All Tour Logs: userDetails is null!");
            throw new RuntimeException("Unauthorized: user not authenticated");
        }

        UUID userId = userDetails.getUserId();
        logger.info("Get All Tour Logs called by user {}", userId);
        return service.findAllByUser(userId);
    }

    // Get a single tour log by ID for authenticated user
    @GetMapping("/{id}")
    public TourLogEntity getById(
            @PathVariable UUID id,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) {
        if (userDetails == null) {
            logger.warn("Get Tour Log {}: userDetails is null!", id);
            throw new RuntimeException("Unauthorized: user not authenticated");
        }

        UUID userId = userDetails.getUserId();
        logger.info("Get Tour Log {} called by user {}", id, userId);

        return service.findByIdAndUser(id, userId)
                .orElseThrow(() -> new RuntimeException("Tour Log not found or access denied"));
    }

    // Create a new tour log
    @PostMapping
    public TourLogEntity create(
            @RequestBody TourLogCreateRequest req,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) {
        if (userDetails == null) {
            logger.warn("Create Tour Log: userDetails is null!");
            throw new RuntimeException("Unauthorized: user not authenticated");
        }

        UUID userId = userDetails.getUserId();
        logger.info("Create Tour Log called by user {}", userId);
        logger.debug("Create Tour Log payload: {}", req);

        TourLogEntity entity = new TourLogEntity(
                UUIDv7.randomUUID(),
                req.tourId(),
                userId,
                req.dateTime(),
                req.rating(),
                req.comment()
        );

        return service.save(entity);
    }

    // Delete a tour log by ID for authenticated user
    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) {
        if (userDetails == null) {
            logger.warn("Delete Tour Log {}: userDetails is null!", id);
            throw new RuntimeException("Unauthorized: user not authenticated");
        }

        UUID userId = userDetails.getUserId();
        logger.info("Delete Tour Log {} called by user {}", id, userId);
        service.deleteByUser(id, userId);
    }
}