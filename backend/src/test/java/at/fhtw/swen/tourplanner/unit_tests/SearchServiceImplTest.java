package at.fhtw.swen.tourplanner.unit_tests;

import at.fhtw.swen.tourplanner.controller.response.SearchResultResponse;
import at.fhtw.swen.tourplanner.controller.response.enums.SearchResultType;
import at.fhtw.swen.tourplanner.persistence.entity.Tour;
import at.fhtw.swen.tourplanner.persistence.entity.TourLog;
import at.fhtw.swen.tourplanner.persistence.repository.SearchRepository;
import at.fhtw.swen.tourplanner.persistence.repository.SearchResultRow;
import at.fhtw.swen.tourplanner.persistence.repository.TourLogRepository;
import at.fhtw.swen.tourplanner.persistence.repository.TourRepository;
import at.fhtw.swen.tourplanner.service.impl.SearchServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SearchServiceImplTest {

    private SearchRepository searchRepository;
    private TourRepository tourRepository;
    private TourLogRepository tourLogRepository;

    private SearchServiceImpl searchService;

    private UUID userId;

    @BeforeEach
    void setUp() {
        searchRepository = mock(SearchRepository.class);
        tourRepository = mock(TourRepository.class);
        tourLogRepository = mock(TourLogRepository.class);

        searchService = new SearchServiceImpl(
                searchRepository,
                tourRepository,
                tourLogRepository
        );

        userId = UUID.randomUUID();
    }

    @Test
    void search_shouldReturnEmptyList_whenQueryIsBlank() {
        List<SearchResultResponse> result = searchService.search(userId, "   ", "all");
        assertTrue(result.isEmpty());
        verifyNoInteractions(searchRepository);
    }

    @Test
    void search_shouldReturnEmpty_whenNoPrefixQueryGenerated() {
        List<SearchResultResponse> result = searchService.search(userId, "!!!", "all");
        assertTrue(result.isEmpty());
        verifyNoInteractions(searchRepository);
    }

    @Test
    void search_shouldReturnTextResults_only_whenToursExcluded() {
        SearchResultRow row = mock(SearchResultRow.class);
        when(row.type()).thenReturn("TOUR");
        when(row.id()).thenReturn("1");
        when(row.tourId()).thenReturn("1");
        when(row.title()).thenReturn("Tour A");
        when(row.snippet()).thenReturn("Snippet");

        when(searchRepository.search(userId, "vienna:*", false, true))
                .thenReturn(List.of(row));

        List<SearchResultResponse> result =
                searchService.search(userId, "Vienna", "logs");

        assertEquals(1, result.size());
        assertEquals(SearchResultType.TOUR, result.getFirst().type());

        verify(searchRepository).search(userId, "vienna:*", false, true);
    }

    @Test
    void search_shouldReturnComputedMatches_whenIntentMatches() {

        UUID tourId = UUID.randomUUID();

        Tour tour = mock(Tour.class);
        when(tour.getId()).thenReturn(tourId);
        when(tour.getName()).thenReturn("Family Tour");

        TourLog log = mock(TourLog.class);
        when(log.getTourId()).thenReturn(tourId);
        when(log.getDifficulty()).thenReturn(1);
        when(log.getTotalDistance()).thenReturn(3);
        when(log.getTotalTime()).thenReturn(60);

        when(tourRepository.findAllByCreatedBy(userId))
                .thenReturn(List.of(tour));

        when(tourLogRepository.findAllByCreatedBy(userId))
                .thenReturn(List.of(log));

        when(searchRepository.search(any(), any(), anyBoolean(), anyBoolean()))
                .thenReturn(List.of());

        List<SearchResultResponse> result =
                searchService.search(userId, "very child friendly", "tours");

        assertFalse(result.isEmpty());

        assertTrue(
                result.stream().anyMatch(r ->
                        r.title().equals("Family Tour")
                )
        );
    }

    @Test
    void search_shouldMergeResults_withoutDuplicates() {

        UUID tourId = UUID.randomUUID();

        SearchResultRow row = mock(SearchResultRow.class);
        when(row.type()).thenReturn("TOUR");
        when(row.id()).thenReturn(tourId.toString());
        when(row.tourId()).thenReturn(tourId.toString());
        when(row.title()).thenReturn("Tour A");
        when(row.snippet()).thenReturn("snippet");

        when(searchRepository.search(any(), any(), anyBoolean(), anyBoolean()))
                .thenReturn(List.of(row));

        Tour tour = mock(Tour.class);
        when(tour.getId()).thenReturn(tourId);
        when(tour.getName()).thenReturn("Tour A");

        when(tourRepository.findAllByCreatedBy(userId))
                .thenReturn(List.of(tour));

        TourLog log = mock(TourLog.class);
        when(log.getTourId()).thenReturn(tourId);
        when(log.getDifficulty()).thenReturn(2);
        when(log.getTotalDistance()).thenReturn(10);
        when(log.getTotalTime()).thenReturn(100);

        when(tourLogRepository.findAllByCreatedBy(userId))
                .thenReturn(List.of(log));

        List<SearchResultResponse> result =
                searchService.search(userId, "tour", "all");

        assertNotNull(result);
        assertFalse(result.isEmpty());

        long distinct = result.stream()
                .map(r -> r.type() + ":" + r.id())
                .distinct()
                .count();

        assertEquals(result.size(), distinct);
    }
}