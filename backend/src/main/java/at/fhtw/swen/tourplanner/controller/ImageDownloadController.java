package at.fhtw.swen.tourplanner.controller;

import at.fhtw.swen.tourplanner.persistence.entity.Tour;
import at.fhtw.swen.tourplanner.persistence.port.UserRepository;
import at.fhtw.swen.tourplanner.persistence.repository.TourJpaRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.UUID;

@RestController
public class ImageDownloadController {

    private final TourJpaRepository tourRepository;
    private final UserRepository userRepository;

    public ImageDownloadController(TourJpaRepository tourRepository, UserRepository userRepository) {
        this.tourRepository = tourRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/api/tours/{tourId}/image")
    public ResponseEntity<Resource> downloadTourImage(
            @PathVariable UUID tourId,
            Principal principal
    ) throws Exception {

        UUID userId = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        Tour tour = tourRepository.findByIdAndCreatedBy(tourId, userId)
                .orElseThrow(() -> new RuntimeException("Tour not found or access denied"));

        if (tour.getImagePath() == null) {
            throw new RuntimeException("No image uploaded for this tour");
        }

        Path path = Paths.get(tour.getImagePath());

        if (!Files.exists(path)) {
            throw new RuntimeException("Image file missing on disk");
        }

        Resource resource = new UrlResource(path.toUri());

        String contentType = Files.probeContentType(path);
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }
}