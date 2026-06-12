package at.fhtw.swen.tourplanner.controller;

import at.fhtw.swen.tourplanner.controller.response.TourListResponse;
import at.fhtw.swen.tourplanner.controller.response.TourTypeListResponse;
import at.fhtw.swen.tourplanner.security.spring.JwtUserDetails;
import at.fhtw.swen.tourplanner.service.TourExportService;
import at.fhtw.swen.tourplanner.service.TourService;
import at.fhtw.swen.tourplanner.service.dto.TourDto;
import at.fhtw.swen.tourplanner.service.dto.TourSortOption;
import at.fhtw.swen.tourplanner.service.dto.export.TourExportDto;
import at.fhtw.swen.tourplanner.util.LoggerService;
import at.fhtw.swen.tourplanner.util.UUIDv7;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tours")
public class TourController {

    private final TourService service;

    private final TourExportService tourExportService;

    private final Logger logger;

    public TourController(
            TourService service,
            TourExportService tourExportService
    ) {

        this.service = service;
        this.tourExportService = tourExportService;

        this.logger =
                LoggerService.getInstance().getLogger(getClass());
    }

    @PostMapping
    public TourDto create(
            @Valid @RequestBody TourDto req,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) {

        UUID userId = userDetails.getUserId();

        UUID id = UUIDv7.randomUUID();

        logger.info(
                "Create Tour called by user: {}",
                userId
        );

        return service.create(userId, req, id);
    }

    @GetMapping
    public List<TourListResponse> all(
            @RequestParam(
                    name = "sort",
                    defaultValue = "POPULARITY_DESC"
            )
            TourSortOption sort,

            @AuthenticationPrincipal JwtUserDetails userDetails
    ) {

        UUID userId = userDetails.getUserId();

        logger.info(
                "Get All Tours called by user: {}, sort={}",
                userId,
                sort
        );

        return service.findAllByUser(userId, sort);
    }

    @GetMapping("/favorites")
    public List<TourTypeListResponse> allFav(
            @RequestParam(
                    name = "sort",
                    defaultValue = "POPULARITY_DESC"
            )
            TourSortOption sort,

            @AuthenticationPrincipal JwtUserDetails userDetails
    ) {

        UUID userId = userDetails.getUserId();

        logger.info(
                "Get All favorite Tours called by user: {}, sort={}",
                userId,
                sort
        );

        return service.findAllFavByUser(userId, sort);
    }

    @GetMapping("/not-favorites")
    public List<TourTypeListResponse> allNotFav(
            @RequestParam(
                    name = "sort",
                    defaultValue = "POPULARITY_DESC"
            )
            TourSortOption sort,

            @AuthenticationPrincipal JwtUserDetails userDetails
    ) {

        UUID userId = userDetails.getUserId();

        logger.info(
                "Get All not favorite Tours called by user: {}, sort={}",
                userId,
                sort
        );

        return service.findAllNotFavByUser(userId, sort);
    }

    @GetMapping("/{id}")
    public Object getById(
            @PathVariable UUID id,

            @RequestParam(
                    name = "withFavorite",
                    defaultValue = "false"
            )
            boolean withFavorite,

            @AuthenticationPrincipal JwtUserDetails userDetails
    ) {

        UUID userId = userDetails.getUserId();

        logger.info(
                "Get Tour {} called by user {}, withFavorite={}",
                id,
                userId,
                withFavorite
        );

        if (withFavorite) {

            return service.findByIdAndUserWithFavorite(
                    id,
                    userId
            );

        } else {

            return service.findByIdAndUser(
                    id,
                    userId
            );
        }
    }

    @GetMapping("/{id}/export")
    public ResponseEntity<String> exportTour(
            @PathVariable UUID id,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) {

        UUID userId = userDetails.getUserId();

        logger.info(
                "Export Tour {} called by user {}",
                id,
                userId
        );

        String xml = tourExportService.exportTour(id);

        return ResponseEntity.ok()
                .contentType(
                        new MediaType(
                                MediaType.APPLICATION_XML,
                                StandardCharsets.UTF_8
                        )
                )
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"tour-" + id + ".xml\""
                )
                .body(xml);
    }

    @PostMapping(
            value = "/import",
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public TourDto importTour(
            @RequestBody TourExportDto exportDto,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) {
        UUID userId = userDetails.getUserId();

        logger.info("Import Tour called by user {}", userId);

        return tourExportService.importTour(userId, exportDto);
    }

    @PutMapping("/{id}")
    public TourDto update(
            @PathVariable UUID id,
            @Valid @RequestBody TourDto req,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) {

        UUID userId = userDetails.getUserId();

        logger.info(
                "Update Tour {} called by user {}",
                id,
                userId
        );

        return service.update(id, userId, req);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) {

        UUID userId = userDetails.getUserId();

        logger.info(
                "Delete Tour {} called by user {}",
                id,
                userId
        );

        service.deleteByUser(id, userId);
    }
}