package at.fhtw.swen.tourplanner;

import at.fhtw.swen.tourplanner.persistence.entity.TourLog;
import at.fhtw.swen.tourplanner.persistence.repository.TourLogRepository;
import at.fhtw.swen.tourplanner.service.dto.TourLogDto;
import at.fhtw.swen.tourplanner.service.impl.TourLogServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TourLogServiceImplTest {

    private TourLogRepository repository;
    private TourLogServiceImpl service;

    private UUID userId;
    private UUID logId;

    @BeforeEach
    void setUp() {
        repository = mock(TourLogRepository.class);
        service = new TourLogServiceImpl(repository);

        userId = UUID.randomUUID();
        logId = UUID.randomUUID();
    }

    @Test
    void findAllByUser_shouldReturnMappedList() {
        TourLog log = mock(TourLog.class);

        when(repository.findAllByCreatedBy(userId))
                .thenReturn(List.of(log));

        List<TourLogDto> result = service.findAllByUser(userId);

        assertNotNull(result);
        assertFalse(result.isEmpty());

        verify(repository).findAllByCreatedBy(userId);
    }

    @Test
    void create_shouldSaveAndReturnDto() {
        TourLogDto dto = mock(TourLogDto.class);

        when(repository.save(any(TourLog.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        TourLogDto result = service.create(dto, userId);

        assertNotNull(result);
        verify(repository).save(any(TourLog.class));
    }

    @Test
    void delete_shouldCallRepository_whenLogExists() {
        TourLog log = mock(TourLog.class);

        when(repository.findByIdAndCreatedBy(logId, userId))
                .thenReturn(Optional.of(log));

        service.deleteByUser(logId, userId);

        verify(repository).deleteByIdAndCreatedBy(logId, userId);
    }
}