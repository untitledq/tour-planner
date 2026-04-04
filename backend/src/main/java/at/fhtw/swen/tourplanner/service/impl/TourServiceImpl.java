package at.fhtw.swen.tourplanner.service.impl;

import at.fhtw.swen.tourplanner.persistence.entity.TourType;
import at.fhtw.swen.tourplanner.service.RouteServiceClient;
import at.fhtw.swen.tourplanner.service.dto.TourDto;
import at.fhtw.swen.tourplanner.controller.response.TourListResponse;
import at.fhtw.swen.tourplanner.service.dto.TourWithFavoriteDto;
import at.fhtw.swen.tourplanner.persistence.entity.Route;
import at.fhtw.swen.tourplanner.persistence.entity.Tour;
import at.fhtw.swen.tourplanner.persistence.repository.RouteJpaRepository;
import at.fhtw.swen.tourplanner.persistence.repository.TourJpaRepository;
import at.fhtw.swen.tourplanner.persistence.repository.UserFavoriteTourJpaRepository;
import at.fhtw.swen.tourplanner.service.TourService;
import at.fhtw.swen.tourplanner.service.mapper.TourMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class TourServiceImpl implements TourService {

    private final TourJpaRepository repository;
    private final RouteJpaRepository routeRepository;
    private final UserFavoriteTourJpaRepository favoriteRepository;
    private final RouteServiceClient routeClient;

    public TourServiceImpl(TourJpaRepository repository,
                           RouteJpaRepository routeRepository,
                           UserFavoriteTourJpaRepository favoriteRepository,
                           RouteServiceClient routeClient) {
        this.repository = repository;
        this.routeRepository = routeRepository;
        this.favoriteRepository = favoriteRepository;
        this.routeClient = routeClient;
    }

    @Override
    public TourDto create(UUID userId, TourDto req, UUID id) {
        Tour tour = TourMapper.fromCreateRequest(id, userId, req);
        repository.save(tour);

        createOrUpdateRoute(tour);

        return TourMapper.toResponse(tour);
    }

    @Override
    public List<TourListResponse> findAllByUser(UUID userId) {
        List<Tour> tours = repository.findAllByCreatedBy(userId);
        return TourMapper.toListResponseList(tours);
    }

    @Override
    public TourDto findByIdAndUser(UUID id, UUID userId) {
        Tour tour = repository.findByIdAndCreatedBy(id, userId)
                .orElseThrow(() -> new RuntimeException("Tour not found or access denied"));

        return TourMapper.toResponse(tour);
    }

    @Override
    public TourWithFavoriteDto findByIdAndUserWithFavorite(UUID tourId, UUID userId) {
        Tour tour = repository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tour not found"));

        boolean isFavorite = favoriteRepository.existsByUserIdAndTourId(userId, tourId);

        return TourMapper.toWithFavoriteResponse(tour, isFavorite);
    }

    @Override
    public TourDto update(UUID id, UUID userId, TourDto tourDto) {
        Tour tour = repository.findByIdAndCreatedBy(id, userId)
                .orElseThrow(() -> new RuntimeException("Tour not found or access denied"));

        boolean routeRelevantChanged = !Objects.equals(tour.getTourType(), tourDto.getTourType()) ||
                        !Objects.equals(tour.getStartName(), tourDto.getStartName()) ||
                        !Objects.equals(tour.getEndName(), tourDto.getEndName());

        TourMapper.updateEntity(tourDto, tour);
        repository.save(tour);

        if (routeRelevantChanged) {
            createOrUpdateRoute(tour);
        }

        return TourMapper.toResponse(tour);
    }

    @Override
    @Transactional
    public void deleteByUser(UUID id, UUID userId) {
        if (repository.findByIdAndCreatedBy(id, userId).isEmpty()) {
            throw new RuntimeException("Tour not found or access denied");
        }
        repository.deleteByIdAndCreatedBy(id, userId);
    }

    private void createOrUpdateRoute(Tour tour) {

        var routeResponse = routeClient.calculateRoute(
                tour.getStartName(),
                tour.getEndName(),
                String.valueOf(tour.getTourType())
        );

        Route route = routeRepository.findByTourId(tour.getId())
                .orElseGet(() -> {
                    Route r = new Route();
                    r.setId(UUID.randomUUID());
                    r.setTourId(tour.getId());
                    return r;
                });

        route.setDistance(routeResponse.getDistance());
        route.setDuration(routeResponse.getDuration());
        route.setCoordinates(routeResponse.getCoordinates());

        routeRepository.save(route);
    }
}