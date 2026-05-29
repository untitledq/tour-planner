package at.fhtw.swen.tourplanner;

import at.fhtw.swen.tourplanner.persistence.repository.TourRepository;
import at.fhtw.swen.tourplanner.persistence.repository.UserFavoriteTourRepository;
import at.fhtw.swen.tourplanner.service.impl.FavoriteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class FavoriteServiceImplTest {

    private UserFavoriteTourRepository favoriteRepository;
    private TourRepository tourRepository;
    private FavoriteServiceImpl service;

    private UUID userId;
    private UUID tourId;

    @BeforeEach
    void setUp() {
        favoriteRepository = mock(UserFavoriteTourRepository.class);
        tourRepository = mock(TourRepository.class);

        service = new FavoriteServiceImpl(favoriteRepository, tourRepository);

        userId = UUID.randomUUID();
        tourId = UUID.randomUUID();
    }

    @Test
    void addFavorite_shouldSave_whenNotExisting() {
        when(tourRepository.existsById(tourId)).thenReturn(true);
        when(favoriteRepository.existsByUserIdAndTourId(userId, tourId))
                .thenReturn(false);

        service.addFavorite(userId, tourId);

        verify(favoriteRepository).save(any());
    }

    @Test
    void addFavorite_shouldThrow_whenTourMissing() {
        when(tourRepository.existsById(tourId)).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> service.addFavorite(userId, tourId));
    }

    @Test
    void removeFavorite_shouldCallRepository() {
        service.removeFavorite(userId, tourId);

        verify(favoriteRepository).deleteByUserIdAndTourId(userId, tourId);
    }
}