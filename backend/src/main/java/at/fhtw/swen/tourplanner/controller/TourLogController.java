package at.fhtw.swen.tourplanner.controller;

import at.fhtw.swen.tourplanner.controller.request.TourLogCreateRequest;
import at.fhtw.swen.tourplanner.controller.request.TourLogUpdateRequest;
import at.fhtw.swen.tourplanner.controller.response.TourLogListResponse;
import at.fhtw.swen.tourplanner.controller.response.TourLogResponse;
import at.fhtw.swen.tourplanner.security.JwtUserDetails;
import at.fhtw.swen.tourplanner.service.impl.TourLogServiceImpl;
import at.fhtw.swen.tourplanner.util.LoggerService;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tour-logs")
public class TourLogController {

    private final TourLogServiceImpl service;
    private final Logger logger;

    public TourLogController(TourLogServiceImpl service) {
        this.service = service;
        this.logger = new LoggerService().getInstance().getLogger(getClass());
    }

    // Get all tour logs for authenticated user
    @GetMapping
    public ResponseEntity<List<TourLogListResponse>> all(
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) {
        UUID userId = userDetails.getUserId();
        logger.info("Get All Tour Logs called by user {}", userId);

        return ResponseEntity.ok(service.findAllByUser(userId));
    }

    // Get a single tour log by ID
    @GetMapping("/{id}")
    public ResponseEntity<TourLogResponse> getById(
            @PathVariable UUID id,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) {
        UUID userId = userDetails.getUserId();
        logger.info("Get Tour Log {} called by user {}", id, userId);

        return ResponseEntity.ok(service.findByIdAndUser(id, userId));
    }

    // Create new tour log
    @PostMapping
    public ResponseEntity<TourLogResponse> create(
            @RequestBody TourLogCreateRequest req,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) {
        UUID userId = userDetails.getUserId();
        logger.info("Create Tour Log called by user {}", userId);

        return ResponseEntity.ok(service.create(req, userId));
    }

    // Update tour log
    @PutMapping("/{id}")
    public ResponseEntity<TourLogResponse> update(
            @PathVariable UUID id,
            @RequestBody TourLogUpdateRequest req,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) {
        UUID userId = userDetails.getUserId();
        logger.info("Update Tour Log {} called by user {}", id, userId);

        return ResponseEntity.ok(service.update(id, req, userId));
    }

    // Delete tour log
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) {
        UUID userId = userDetails.getUserId();
        logger.info("Delete Tour Log {} called by user {}", id, userId);

        service.deleteByUser(id, userId);
        return ResponseEntity.noContent().build();
    }
}