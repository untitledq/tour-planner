package at.fhtw.swen.tourplanner.service.impl;

import at.fhtw.swen.tourplanner.config.ImageStorageProperties;
import at.fhtw.swen.tourplanner.service.ImageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService {

    private final Path basePath;

    public ImageServiceImpl(ImageStorageProperties props) {
        this.basePath = Paths.get(props.getBaseDir());
    }

    @Override
    public String saveTourImage(UUID tourId, MultipartFile file, String frontendUsername) {
        try {
            String safeUsername = sanitizeUsername(frontendUsername);
            Path userFolder = basePath.resolve(safeUsername);
            Files.createDirectories(userFolder);

            String extension = getExtension(file.getOriginalFilename());
            String filename = tourId + "." + extension;

            Path target = userFolder.resolve(filename);

            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            return target.toString();

        } catch (IOException e) {
            throw new RuntimeException("Could not store image", e);
        }
    }

}