package at.fhtw.swen.tourplanner.unit_tests;

import at.fhtw.swen.tourplanner.persistence.entity.Tour;
import at.fhtw.swen.tourplanner.controller.exception.TourNotFoundException;
import at.fhtw.swen.tourplanner.persistence.repository.adapter.TourRepositoryAdapter;
import at.fhtw.swen.tourplanner.persistence.repository.adapter.UserFavoriteTourRepositoryAdapter;
import at.fhtw.swen.tourplanner.service.impl.FavoriteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FavoriteServiceImplTest {

    private UserFavoriteTourRepositoryAdapter favoriteRepositoryAdapter;
    private TourRepositoryAdapter tourRepositoryAdapter;
    private FavoriteServiceImpl service;

    private UUID userId;
    private UUID tourId;

    @BeforeEach
    void setUp() {
        favoriteRepositoryAdapter = mock(UserFavoriteTourRepositoryAdapter.class);
        tourRepositoryAdapter = mock(TourRepositoryAdapter.class);

        service = new FavoriteServiceImpl(
                favoriteRepositoryAdapter,
                tourRepositoryAdapter
        );

        userId = UUID.randomUUID();
        tourId = UUID.randomUUID();
    }

    @Test
    void addFavorite_shouldSave_whenNotExisting() {

        Tour tour = mock(Tour.class);

        when(tourRepositoryAdapter.findById(tourId))
                .thenReturn(Optional.ofNullable(tour));

        when(favoriteRepositoryAdapter.existsByUserIdAndTourId(userId, tourId))
                .thenReturn(false);

        service.addFavorite(userId, tourId);

        verify(favoriteRepositoryAdapter).save(any());
    }

    @Test
    void addFavorite_shouldThrow_whenTourMissing() {

        when(tourRepositoryAdapter.findById(tourId))
                .thenThrow(new TourNotFoundException(
                        "Tour with id " + tourId + " not found"));

        assertThrows(
                TourNotFoundException.class,
                () -> service.addFavorite(userId, tourId)
        );
    }

    @Test
    void removeFavorite_shouldCallRepository() {

        service.removeFavorite(userId, tourId);

        verify(favoriteRepositoryAdapter)
                .deleteByUserIdAndTourId(userId, tourId);
    }

    @Test
    void addFavorite_shouldNotSave_whenAlreadyFavorite() {

        Tour tour = mock(Tour.class);

        when(tourRepositoryAdapter.findById(tourId))
                .thenReturn(Optional.ofNullable(tour));

        when(favoriteRepositoryAdapter.existsByUserIdAndTourId(userId, tourId))
                .thenReturn(true);

        service.addFavorite(userId, tourId);

        verify(favoriteRepositoryAdapter, never()).save(any());
    }
}