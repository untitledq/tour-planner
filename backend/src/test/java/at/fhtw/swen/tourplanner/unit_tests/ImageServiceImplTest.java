package at.fhtw.swen.tourplanner.unit_tests;

import at.fhtw.swen.tourplanner.persistence.entity.Tour;
import at.fhtw.swen.tourplanner.persistence.repository.TourRepository;
import at.fhtw.swen.tourplanner.service.ImageStorageProperties;
import at.fhtw.swen.tourplanner.service.impl.ImageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ImageServiceImplTest {

    private TourRepository tourRepository;
    private ImageServiceImpl service;

    private UUID tourId;
    private UUID userId;

    private Path tempDir;

    @BeforeEach
    void setUp() throws Exception {
        tourRepository = mock(TourRepository.class);

        ImageStorageProperties props = mock(ImageStorageProperties.class);

        tempDir = Files.createTempDirectory("tour-images-test");
        when(props.getBaseDir()).thenReturn(tempDir.toString());

        service = new ImageServiceImpl(props, tourRepository);

        tourId = UUID.randomUUID();
        userId = UUID.randomUUID();
    }

    @Test
    void download_shouldReturnResource_whenImageExists() throws Exception {
        Tour tour = mock(Tour.class);

        Path file = Files.createTempFile(tempDir, "img", ".jpg");
        Files.writeString(file, "data");

        when(tour.getImagePath()).thenReturn(file.toString());
        when(tourRepository.findByIdAndCreatedBy(tourId, userId))
                .thenReturn(Optional.of(tour));

        Resource resource = service.downloadTourImage(tourId, userId);

        assertNotNull(resource);
        assertTrue(resource.exists());
    }

    @Test
    void download_shouldThrow_whenNoImagePath() {
        Tour tour = mock(Tour.class);

        when(tour.getImagePath()).thenReturn(null);

        when(tourRepository.findByIdAndCreatedBy(tourId, userId))
                .thenReturn(Optional.of(tour));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.downloadTourImage(tourId, userId)
        );

        assertEquals("No image uploaded", ex.getMessage());
    }
}