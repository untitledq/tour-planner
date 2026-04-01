package at.fhtw.swen.tourplanner.controller;

import at.fhtw.swen.tourplanner.controller.response.ImageUploadResponse;
import at.fhtw.swen.tourplanner.persistence.entity.Tour;
import at.fhtw.swen.tourplanner.persistence.port.UserRepository;
import at.fhtw.swen.tourplanner.persistence.repository.TourJpaRepository;
import at.fhtw.swen.tourplanner.service.ImageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.UUID;

@RestController
public class ImageUploadController {

    private final ImageService imageService;
    private final TourJpaRepository tourRepository;
    private final UserRepository userRepository;

    public ImageUploadController(ImageService imageService,
                                 TourJpaRepository tourRepository,
                                 UserRepository userRepository) {
        this.imageService = imageService;
        this.tourRepository = tourRepository;
        this.userRepository = userRepository;
    }

    @PostMapping(
            value = "/api/tours/{tourId}/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ImageUploadResponse> uploadTourImage(
            @PathVariable UUID tourId,
            @RequestPart("file") MultipartFile file,
            Principal principal
    ) {

        UUID userId = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        Tour tour = tourRepository.findByIdAndCreatedBy(tourId, userId)
                .orElseThrow(() -> new RuntimeException("Tour not found or access denied"));

        String path = imageService.saveTourImage(tourId, file, principal.getName());

        tour.setImagePath(path);
        tourRepository.save(tour);

        return ResponseEntity.ok(new ImageUploadResponse(true));
    }
}