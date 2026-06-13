package at.fhtw.swen.tourplanner.unit_tests;

import at.fhtw.swen.tourplanner.persistence.entity.Tour;
import at.fhtw.swen.tourplanner.persistence.repository.adapter.TourRepositoryAdapter;
import at.fhtw.swen.tourplanner.service.ImageStorageProperties;
import at.fhtw.swen.tourplanner.service.exception.TourImageMissingException;
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

    private TourRepositoryAdapter tourRepositoryAdapter;
    private ImageServiceImpl service;

    private UUID tourId;
    private UUID userId;

    private Path tempDir;

    @BeforeEach
    void setUp() throws Exception {

        tourRepositoryAdapter = mock(TourRepositoryAdapter.class);

        ImageStorageProperties props = mock(ImageStorageProperties.class);

        tempDir = Files.createTempDirectory("tour-images-test");
        when(props.getBaseDir()).thenReturn(tempDir.toString());

        service = new ImageServiceImpl(
                props,
                tourRepositoryAdapter
        );

        tourId = UUID.randomUUID();
        userId = UUID.randomUUID();
    }

    @Test
    void download_shouldReturnResource_whenImageExists() throws Exception {

        Tour tour = mock(Tour.class);

        Path file = Files.createTempFile(tempDir, "img", ".jpg");
        Files.writeString(file, "data");

        when(tour.getImagePath()).thenReturn(file.toString());

        when(tourRepositoryAdapter.findByIdAndCreatedBy(tourId, userId))
                .thenReturn(Optional.of(tour));

        Resource resource = service.downloadTourImage(tourId, userId);

        assertNotNull(resource);
        assertTrue(resource.exists());

        verify(tourRepositoryAdapter)
                .findByIdAndCreatedBy(tourId, userId);
    }

    @Test
    void download_shouldThrow_whenNoImagePath() {

        Tour tour = mock(Tour.class);

        when(tour.getImagePath()).thenReturn(null);

        when(tourRepositoryAdapter.findByIdAndCreatedBy(tourId, userId))
                .thenReturn(Optional.of(tour));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.downloadTourImage(tourId, userId)
        );

        assertTrue(ex instanceof TourImageMissingException);
        assertEquals("No image uploaded for tour " + tourId, ex.getMessage());

        verify(tourRepositoryAdapter)
                .findByIdAndCreatedBy(tourId, userId);
    }
}