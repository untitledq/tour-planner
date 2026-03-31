package at.fhtw.swen.tourplanner.service.impl;

import at.fhtw.swen.tourplanner.config.ImageStorageProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ImageServiceImpl {

    private final Path basePath;

    public ImageServiceImpl(ImageStorageProperties props) {
        this.basePath = Paths.get(props.getBaseDir());
    }

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

    private String getExtension(String name) {
        if (name == null || !name.contains(".")) {
            return "jpg";
        }
        return name.substring(name.lastIndexOf('.') + 1);
    }

    private String sanitizeUsername(String username) {
        return username.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}