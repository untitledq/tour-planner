package at.fhtw.swen.tourplanner.unit_tests;

import at.fhtw.swen.tourplanner.controller.response.TourListResponse;
import at.fhtw.swen.tourplanner.persistence.entity.Route;
import at.fhtw.swen.tourplanner.persistence.entity.Tour;
import at.fhtw.swen.tourplanner.persistence.entity.TourType;
import at.fhtw.swen.tourplanner.persistence.repository.adapter.RouteRepositoryAdapter;
import at.fhtw.swen.tourplanner.persistence.repository.adapter.TourLogRepositoryAdapter;
import at.fhtw.swen.tourplanner.persistence.repository.adapter.TourRepositoryAdapter;
import at.fhtw.swen.tourplanner.persistence.repository.adapter.UserFavoriteTourRepositoryAdapter;
import at.fhtw.swen.tourplanner.service.RouteServiceClient;
import at.fhtw.swen.tourplanner.service.dto.TourDto;
import at.fhtw.swen.tourplanner.service.impl.TourServiceImpl;
import at.fhtw.swen.tourplanner.service.dto.TourSortOption;
import at.fhtw.swen.tourplanner.service.dto.RouteDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TourServiceImplTest {

    private TourRepositoryAdapter tourRepositoryAdapter;
    private RouteRepositoryAdapter routeRepositoryAdapter;
    private TourLogRepositoryAdapter tourLogRepositoryAdapter;
    private UserFavoriteTourRepositoryAdapter userFavoriteTourRepositoryAdapter;
    private RouteServiceClient routeClient;

    private TourServiceImpl service;

    private UUID userId;

    @BeforeEach
    void setUp() {
        tourRepositoryAdapter = mock(TourRepositoryAdapter.class);
        routeRepositoryAdapter = mock(RouteRepositoryAdapter.class);
        tourLogRepositoryAdapter = mock(TourLogRepositoryAdapter.class);
        userFavoriteTourRepositoryAdapter = mock(UserFavoriteTourRepositoryAdapter.class);
        routeClient = mock(RouteServiceClient.class);

        service = new TourServiceImpl(
                tourRepositoryAdapter,
                routeRepositoryAdapter,
                tourLogRepositoryAdapter,
                userFavoriteTourRepositoryAdapter,
                routeClient
        );

        userId = UUID.randomUUID();
    }

    @Test
    void create_shouldPersistTour_andCreateRoute() {
        TourDto dto = mock(TourDto.class);

        Tour tour = mock(Tour.class);
        UUID tourId = UUID.randomUUID();

        when(routeClient.calculateRoute(any(), any(), any()))
                .thenReturn(new RouteDto(1000, 600, List.of(List.of(1.0, 2.0))));

        when(tour.getId()).thenReturn(tourId);

        when(tourRepositoryAdapter.save(any(Tour.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        when(routeRepositoryAdapter.findByTourId(any()))
                .thenReturn(Optional.empty());

        var result = service.create(userId, dto, tourId);

        assertNotNull(result);
        verify(routeClient).calculateRoute(any(), any(), any());
        verify(routeRepositoryAdapter).save(any(Route.class));
    }

    @Test
    void findAllByUser_shouldReturnList() {
        Tour tour = mock(Tour.class);
        when(tour.getId()).thenReturn(UUID.randomUUID());

        when(tourRepositoryAdapter.findAllByCreatedBy(userId))
                .thenReturn(List.of(tour));

        when(tourLogRepositoryAdapter.findAllByCreatedBy(userId))
                .thenReturn(List.of());

        List<TourListResponse> result =
                service.findAllByUser(userId, TourSortOption.NAME_ASC);

        assertNotNull(result);
    }

    @Test
    void findById_shouldReturnFavoriteFlag() {
        UUID tourId = UUID.randomUUID();

        Tour tour = mock(Tour.class);
        when(tour.getId()).thenReturn(tourId);
        when(tour.getName()).thenReturn("Tour A");

        when(tourRepositoryAdapter.findById(tourId))
                .thenReturn(Optional.of(tour));

        when(userFavoriteTourRepositoryAdapter.existsByUserIdAndTourId(userId, tourId))
                .thenReturn(true);

        var result = service.findByIdAndUserWithFavorite(tourId, userId);

        assertNotNull(result);
        verify(userFavoriteTourRepositoryAdapter).existsByUserIdAndTourId(userId, tourId);
    }

    @Test
    void update_shouldRecalculateRoute_whenRelevantFieldsChange() {
        UUID tourId = UUID.randomUUID();

        Tour tour = mock(Tour.class);
        when(tour.getTourType()).thenReturn(null);
        when(tour.getStartName()).thenReturn("A");
        when(tour.getEndName()).thenReturn("B");
        when(tour.getId()).thenReturn(tourId);

        when(tourRepositoryAdapter.findByIdAndCreatedBy(tourId, userId))
                .thenReturn(Optional.of(tour));

        TourDto dto = mock(TourDto.class);
        when(dto.getTourType()).thenReturn(TourType.BIKE);
        when(dto.getStartName()).thenReturn("A");
        when(dto.getEndName()).thenReturn("C"); // triggers route update

        when(routeClient.calculateRoute(any(), any(), any()))
                .thenReturn(new RouteDto(1, 1, List.of(List.of(1.0, 2.0))));

        var result = service.update(tourId, userId, dto);

        assertNotNull(result);
        verify(routeClient).calculateRoute(any(), any(), any());
    }

    @Test
    void delete_shouldCallRepository_whenTourExists() {
        UUID tourId = UUID.randomUUID();

        Tour tour = mock(Tour.class);
        when(tourRepositoryAdapter.findByIdAndCreatedBy(tourId, userId))
                .thenReturn(Optional.of(tour));

        service.deleteByUser(tourId, userId);

        verify(tourRepositoryAdapter).deleteByIdAndCreatedBy(tourId, userId);
    }
}