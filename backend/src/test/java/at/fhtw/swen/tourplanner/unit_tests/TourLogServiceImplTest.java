package at.fhtw.swen.tourplanner.unit_tests;

import at.fhtw.swen.tourplanner.persistence.entity.TourLog;
import at.fhtw.swen.tourplanner.persistence.repository.adapter.TourLogRepositoryAdapter;
import at.fhtw.swen.tourplanner.service.dto.TourLogDto;
import at.fhtw.swen.tourplanner.service.impl.TourLogServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TourLogServiceImplTest {

    private TourLogRepositoryAdapter tourLogRepositoryAdapter;
    private TourLogServiceImpl service;

    private UUID userId;
    private UUID logId;

    @BeforeEach
    void setUp() {
        tourLogRepositoryAdapter = mock(TourLogRepositoryAdapter.class);
        service = new TourLogServiceImpl(tourLogRepositoryAdapter);

        userId = UUID.randomUUID();
        logId = UUID.randomUUID();
    }

    @Test
    void findAllByUser_shouldReturnMappedList() {
        TourLog log = mock(TourLog.class);

        when(tourLogRepositoryAdapter.findAllByCreatedBy(userId))
                .thenReturn(List.of(log));

        List<TourLogDto> result = service.findAllByUser(userId);

        assertNotNull(result);
        assertFalse(result.isEmpty());

        verify(tourLogRepositoryAdapter).findAllByCreatedBy(userId);
    }

    @Test
    void create_shouldSaveAndReturnDto() {
        TourLogDto dto = mock(TourLogDto.class);

        when(tourLogRepositoryAdapter.save(any(TourLog.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        TourLogDto result = service.create(dto, userId);

        assertNotNull(result);
        verify(tourLogRepositoryAdapter).save(any(TourLog.class));
    }

    @Test
    void delete_shouldCallRepository() {
        service.deleteByUser(logId, userId);

        verify(tourLogRepositoryAdapter)
                .deleteByIdAndCreatedBy(logId, userId);
    }
}