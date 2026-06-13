package at.fhtw.swen.tourplanner.controller;

import at.fhtw.swen.tourplanner.security.spring.JwtUserDetails;
import at.fhtw.swen.tourplanner.service.TourLogService;
import at.fhtw.swen.tourplanner.service.dto.TourLogDto;
import at.fhtw.swen.tourplanner.util.LoggerService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tour-logs")
public class TourLogController {

    private final TourLogService service;
    private final Logger logger;

    public TourLogController(TourLogService service) {
        this.service = service;
        this.logger = LoggerService.getInstance().getLogger(getClass());
    }

    @GetMapping
    public ResponseEntity<List<TourLogDto>> all(
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) {
        UUID userId = userDetails.getUserId();
        logger.info("Get All Tour Logs called by user {}", userId);

        return ResponseEntity.ok(service.findAllByUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TourLogDto> getById(
            @PathVariable UUID id,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) {
        UUID userId = userDetails.getUserId();
        logger.info("Get Tour Log {} called by user {}", id, userId);

        return ResponseEntity.ok(service.findByIdAndUser(id, userId));
    }

    @GetMapping("/tour/{tourId}")
    public ResponseEntity<List<TourLogDto>> getByTourId(
            @PathVariable UUID tourId,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) {
        UUID userId = userDetails.getUserId();
        logger.info("Get Tour Logs for tour {} called by user {}", tourId, userId);

        return ResponseEntity.ok(service.findAllByTourAndUser(tourId, userId));
    }

    @PostMapping
    public ResponseEntity<TourLogDto> create(
            @Valid @RequestBody TourLogDto req,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) {
        UUID userId = userDetails.getUserId();
        logger.info("Create Tour Log called by user {}", userId);

        return ResponseEntity.ok(service.create(req, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TourLogDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody TourLogDto req,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) {
        UUID userId = userDetails.getUserId();
        logger.info("Update Tour Log {} called by user {}", id, userId);

        return ResponseEntity.ok(service.update(id, req, userId));
    }

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