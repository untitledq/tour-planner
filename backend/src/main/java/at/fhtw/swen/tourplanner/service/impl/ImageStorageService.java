package at.fhtw.swen.tourplanner.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ImageStorageService {

    // TODO: implement image storage

//    private final Path basePath;
//
//    public ImageStorageService(ImageStorageProperties props) {
//        this.basePath = Paths.get(props.getBaseDir());
//    }
//
//    public String saveTourImage(UUID tourId, MultipartFile file) {
//        try {
//            Files.createDirectories(basePath);
//
//            String extension = getExtension(file.getOriginalFilename());
//            String filename = tourId + "." + extension;
//
//            Path target = basePath.resolve(filename);
//            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
//
//            // THIS is what goes into the DB
//            return target.toString();
//
//        } catch (IOException e) {
//            throw new RuntimeException("Could not store image", e);
//        }
//    }
//
//    private String getExtension(String name) {
//        return name.substring(name.lastIndexOf('.') + 1);
//    }
}