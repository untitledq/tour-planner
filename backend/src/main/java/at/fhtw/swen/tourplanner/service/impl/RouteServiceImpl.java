package at.fhtw.swen.tourplanner.service.impl;

import at.fhtw.swen.tourplanner.persistence.entity.Route;
import at.fhtw.swen.tourplanner.persistence.entity.Tour;
import at.fhtw.swen.tourplanner.persistence.repository.RouteRepositoryAdapter;
import at.fhtw.swen.tourplanner.persistence.repository.TourRepositoryAdapter;
import at.fhtw.swen.tourplanner.service.RouteService;
import at.fhtw.swen.tourplanner.service.RouteServiceClient;
import at.fhtw.swen.tourplanner.service.dto.RouteDto;
import at.fhtw.swen.tourplanner.persistence.exception.TourNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class RouteServiceImpl implements RouteService {

    private final RouteRepositoryAdapter routeRepositoryAdapter;
    private final TourRepositoryAdapter tourRepositoryAdapter;
    private final RouteServiceClient routeClient;

    public RouteServiceImpl(RouteRepositoryAdapter routeRepositoryAdapter,
                            TourRepositoryAdapter tourRepositoryAdapter,
                            RouteServiceClient routeClient) {
        this.routeRepositoryAdapter = routeRepositoryAdapter;
        this.tourRepositoryAdapter = tourRepositoryAdapter;
        this.routeClient = routeClient;
    }

    @Override
    @Transactional(readOnly = true)
    public RouteDto getRouteForTour(UUID tourId, UUID userId) {
        if (tourId == null || userId == null) {
            throw new IllegalArgumentException("Tour ID and User ID must not be null");
        }

        Tour tour = tourRepositoryAdapter.findByIdAndCreatedBy(tourId, userId)
                .orElseThrow(() -> new TourNotFoundException("Tour not found with ID: " + tourId));

        Route route = routeRepositoryAdapter.findByTourId(tour.getId())
                .orElseGet(() -> {
                    RouteDto dto = routeClient.calculateRoute(
                            tour.getStartName(),
                            tour.getEndName(),
                            tour.getTourType().name()
                    );

                    if (dto == null) {
                        throw new RuntimeException("Failed to calculate route");
                    }

                    Route newRoute = new Route(
                            UUID.randomUUID(),
                            tour.getId(),
                            dto.distance(),
                            dto.duration(),
                            dto.coordinates()
                    );

                    return routeRepositoryAdapter.save(newRoute);
                });

        return new RouteDto(
                route.getDistance(),
                route.getDuration(),
                route.getCoordinates()
        );
    }
}