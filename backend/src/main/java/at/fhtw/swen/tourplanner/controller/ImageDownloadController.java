package at.fhtw.swen.tourplanner.controller;

import at.fhtw.swen.tourplanner.persistence.port.UserRepository;
import at.fhtw.swen.tourplanner.service.ImageService;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
public class ImageDownloadController {

    private final ImageService imageService;
    private final UserRepository userRepository;

    public ImageDownloadController(ImageService imageService,
                                   UserRepository userRepository) {
        this.imageService = imageService;
        this.userRepository = userRepository;
    }

    @GetMapping("/api/tours/{tourId}/image")
    public ResponseEntity<Resource> downloadTourImage(
            @PathVariable UUID tourId,
            Principal principal
    ) {

        UUID userId = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        Resource resource = imageService.downloadTourImage(tourId, userId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}