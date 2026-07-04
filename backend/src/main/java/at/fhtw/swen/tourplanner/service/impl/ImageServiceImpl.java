package at.fhtw.swen.tourplanner.service.impl;

import at.fhtw.swen.tourplanner.controller.exception.TourNotFoundException;
import at.fhtw.swen.tourplanner.persistence.repository.adapter.TourRepositoryAdapter;
import at.fhtw.swen.tourplanner.service.ImageStorageProperties;
import at.fhtw.swen.tourplanner.persistence.entity.Tour;
import at.fhtw.swen.tourplanner.service.ImageService;
import at.fhtw.swen.tourplanner.service.dto.TourDto;
import at.fhtw.swen.tourplanner.controller.exception.TourImageMissingException;
import at.fhtw.swen.tourplanner.controller.exception.TourImageNotFoundException;
import at.fhtw.swen.tourplanner.controller.exception.TourImageStorageException;
import at.fhtw.swen.tourplanner.service.mapper.TourMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService {

    private final Path basePath;
    private final TourRepositoryAdapter tourRepositoryAdapter;

    public ImageServiceImpl(
            ImageStorageProperties props,
            TourRepositoryAdapter tourRepositoryAdapter
    ) {
        this.basePath = Paths.get(props.getBaseDir());
        this.tourRepositoryAdapter = tourRepositoryAdapter;
    }

    @Override
    public TourDto uploadTourImage(
            UUID tourId,
            UUID userId,
            String username,
            MultipartFile file
    ) {

        Tour tour = tourRepositoryAdapter.findByIdAndCreatedBy(tourId, userId)
                .orElseThrow(() ->
                        new TourNotFoundException(
                                "Tour not found for id " + tourId
                        ));

        String path = storeFile(
                tourId,
                username,
                file
        );

        tour.setImagePath(path);

        tourRepositoryAdapter.save(tour);

        return TourMapper.toResponse(tour);
    }

    @Override
    public Resource downloadTourImage(
            UUID tourId,
            UUID userId
    ) {

        Tour tour = tourRepositoryAdapter.findByIdAndCreatedBy(tourId, userId)
                .orElseThrow(() ->
                        new TourNotFoundException(
                                "Tour not found for id " + tourId
                        ));

        if (tour.getImagePath() == null) {
            throw new TourImageMissingException(
                    "No image uploaded for tour " + tourId
            );
        }

        try {

            Path path = Paths.get(tour.getImagePath());

            if (!Files.exists(path)) {
                throw new TourImageNotFoundException(
                        "Image file missing on disk for tour " + tourId
                );
            }

            return new UrlResource(path.toUri());

        } catch (TourImageNotFoundException exception) {
            throw exception;

        } catch (Exception exception) {
            throw new TourImageStorageException(
                    "Could not load image for tour " + tourId
            );
        }
    }

    private String storeFile(
            UUID tourId,
            String username,
            MultipartFile file
    ) {

        try {

            String safeUsername =
                    sanitizeUsername(username);

            Path userFolder =
                    basePath.resolve(safeUsername);

            Files.createDirectories(userFolder);

            String extension =
                    getExtension(file.getOriginalFilename());

            String filename =
                    tourId + "." + extension;

            Path target =
                    userFolder.resolve(filename);

            Files.copy(
                    file.getInputStream(),
                    target,
                    StandardCopyOption.REPLACE_EXISTING
            );

            return target.toString();

        } catch (IOException exception) {

            throw new TourImageStorageException(
                    "Could not store image for tour " + tourId
            );
        }
    }

    private String getExtension(String name) {

        if (name == null || !name.contains(".")) {
            return "jpg";
        }

        return name.substring(
                name.lastIndexOf('.') + 1
        );
    }

    private String sanitizeUsername(String username) {
        return username.replaceAll(
                "[^a-zA-Z0-9._-]",
                "_"
        );
    }
}