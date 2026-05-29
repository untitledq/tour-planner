package at.fhtw.swen.tourplanner;

import at.fhtw.swen.tourplanner.persistence.entity.Tour;
import at.fhtw.swen.tourplanner.persistence.entity.TourLog;
import at.fhtw.swen.tourplanner.persistence.entity.TourType;
import at.fhtw.swen.tourplanner.persistence.repository.TourLogRepository;
import at.fhtw.swen.tourplanner.persistence.repository.TourRepository;
import at.fhtw.swen.tourplanner.service.dto.export.TourExportDto;
import at.fhtw.swen.tourplanner.service.dto.export.TourLogExportDto;
import at.fhtw.swen.tourplanner.service.impl.TourExportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TourExportServiceImplTest {

    private TourRepository tourRepository;
    private TourLogRepository tourLogRepository;
    private TourExportServiceImpl service;

    private UUID tourId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        tourRepository = mock(TourRepository.class);
        tourLogRepository = mock(TourLogRepository.class);

        service = new TourExportServiceImpl(tourRepository, tourLogRepository);

        tourId = UUID.randomUUID();
        userId = UUID.randomUUID();
    }

    @Test
    void export_shouldReturnXml_whenTourExists() {
        Tour tour = mock(Tour.class);

        when(tour.getId()).thenReturn(tourId);
        when(tour.getCreatedBy()).thenReturn(userId);
        when(tour.getName()).thenReturn("Vienna Tour");
        when(tour.getDescription()).thenReturn("Nice tour");
        when(tour.getImagePath()).thenReturn("path.jpg");
        when(tour.getTourType()).thenReturn(TourType.BIKE);
        when(tour.getStartName()).thenReturn("Start");
        when(tour.getEndName()).thenReturn("End");

        when(tourRepository.findById(tourId)).thenReturn(Optional.of(tour));
        when(tourLogRepository.findByTourId(tourId)).thenReturn(List.of());

        String xml = service.exportTour(tourId);

        assertNotNull(xml);
        assertTrue(xml.contains("Vienna Tour"));
        assertTrue(xml.contains("BIKE"));

        verify(tourRepository).findById(tourId);
        verify(tourLogRepository).findByTourId(tourId);
    }

    @Test
    void import_shouldCreateTour_andLogs_whenLogsPresent() {
        TourExportDto dto = new TourExportDto();
        dto.setName("Imported Tour");
        dto.setDescription("desc");
        dto.setImagePath("img.jpg");
        dto.setTourType(TourType.BIKE);
        dto.setStartName("A");
        dto.setEndName("B");

        TourLogExportDto log = mock(TourLogExportDto.class);
        when(log.dateTime()).thenReturn(Instant.now());
        when(log.rating()).thenReturn(5);
        when(log.difficulty()).thenReturn(2);
        when(log.totalDistance()).thenReturn(10);
        when(log.totalTime()).thenReturn(60);
        when(log.comment()).thenReturn("good");

        dto.setLogs(List.of(log));

        Tour savedTour = mock(Tour.class);
        when(savedTour.getId()).thenReturn(tourId);
        when(savedTour.getName()).thenReturn("Imported Tour");
        when(savedTour.getDescription()).thenReturn("desc");
        when(savedTour.getImagePath()).thenReturn("img.jpg");
        when(savedTour.getTourType()).thenReturn(TourType.BIKE);
        when(savedTour.getStartName()).thenReturn("A");
        when(savedTour.getEndName()).thenReturn("B");

        when(tourRepository.save(any(Tour.class))).thenReturn(savedTour);

        var result = service.importTour(userId, dto);

        assertNotNull(result);
        assertEquals("Imported Tour", result.getName());

        verify(tourRepository).save(any(Tour.class));
        verify(tourLogRepository, atLeastOnce()).save(any(TourLog.class));
    }

    @Test
    void import_shouldWork_whenNoLogsProvided() {
        TourExportDto dto = new TourExportDto();
        dto.setName("No Logs Tour");
        dto.setTourType(TourType.HIKE);

        when(tourRepository.save(any(Tour.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = service.importTour(userId, dto);

        assertNotNull(result);
        assertEquals("No Logs Tour", result.getName());

        verify(tourLogRepository, never()).save(any());
    }

    @Test
    void export_shouldThrow_whenTourMissing() {
        when(tourRepository.findById(tourId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> service.exportTour(tourId));
    }
}