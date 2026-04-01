package at.fhtw.swen.tourplanner.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface ImageService {
    String saveTourImage(UUID tourId, MultipartFile file, String frontendUsername);

    default String getExtension(String name) {
        if (name == null || !name.contains(".")) {
            return "jpg";
        }
        return name.substring(name.lastIndexOf('.') + 1);
    }

    default String sanitizeUsername(String username) {
        return username.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
