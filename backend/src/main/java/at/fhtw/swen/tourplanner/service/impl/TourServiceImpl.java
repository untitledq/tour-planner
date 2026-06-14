package at.fhtw.swen.tourplanner.service.impl;

import at.fhtw.swen.tourplanner.controller.exception.TourNotFoundException;
import at.fhtw.swen.tourplanner.controller.response.TourTypeListResponse;
import at.fhtw.swen.tourplanner.persistence.repository.adapter.RouteRepositoryAdapter;
import at.fhtw.swen.tourplanner.persistence.repository.adapter.TourLogRepositoryAdapter;
import at.fhtw.swen.tourplanner.persistence.repository.adapter.TourRepositoryAdapter;
import at.fhtw.swen.tourplanner.persistence.repository.adapter.UserFavoriteTourRepositoryAdapter;
import at.fhtw.swen.tourplanner.service.RouteServiceClient;
import at.fhtw.swen.tourplanner.service.dto.TourDto;
import at.fhtw.swen.tourplanner.controller.response.TourListResponse;
import at.fhtw.swen.tourplanner.service.dto.TourWithFavoriteDto;
import at.fhtw.swen.tourplanner.persistence.entity.Route;
import at.fhtw.swen.tourplanner.persistence.entity.Tour;
import at.fhtw.swen.tourplanner.persistence.entity.TourLog;
import at.fhtw.swen.tourplanner.service.TourService;
import at.fhtw.swen.tourplanner.service.dto.ChildFriendliness;
import at.fhtw.swen.tourplanner.service.dto.Popularity;
import at.fhtw.swen.tourplanner.service.dto.TourComputedAttributesDto;
import at.fhtw.swen.tourplanner.service.mapper.TourMapper;
import at.fhtw.swen.tourplanner.service.dto.TourSortOption;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TourServiceImpl implements TourService {

    private final TourRepositoryAdapter tourRepositoryAdapter;
    private final RouteRepositoryAdapter routeRepositoryAdapter;
    private final TourLogRepositoryAdapter tourLogRepositoryAdapter;
    private final UserFavoriteTourRepositoryAdapter userFavoriteTourRepositoryAdapter;
    private final RouteServiceClient routeClient;

    public TourServiceImpl(TourRepositoryAdapter tourRepositoryAdapter,
                           RouteRepositoryAdapter routeRepositoryAdapter,
                           TourLogRepositoryAdapter tourLogRepositoryAdapter,
                           UserFavoriteTourRepositoryAdapter userFavoriteTourRepositoryAdapter,
                           RouteServiceClient routeClient) {
        this.tourRepositoryAdapter = tourRepositoryAdapter;
        this.routeRepositoryAdapter = routeRepositoryAdapter;
        this.tourLogRepositoryAdapter = tourLogRepositoryAdapter;
        this.userFavoriteTourRepositoryAdapter = userFavoriteTourRepositoryAdapter;
        this.routeClient = routeClient;
    }

    @Override
    public TourDto create(UUID userId, TourDto req, UUID id) {
        Tour tour = TourMapper.fromCreateRequest(id, userId, req);
        tourRepositoryAdapter.create(tour);

        createOrUpdateRoute(tour);

        return TourMapper.toResponse(tour);
    }

    @Override
    public List<TourListResponse> findAllByUser(UUID userId, TourSortOption sort) {
        List<Tour> tours = tourRepositoryAdapter.findAllByCreatedBy(userId);
        Map<UUID, TourComputedAttributesDto> attributesByTourId = getComputedAttributesByTourId(userId);

        return sortTours(tours, attributesByTourId, sort).stream()
                .map(tour -> TourMapper.toListResponse(tour, getAttributes(tour, attributesByTourId)))
                .toList();
    }

    @Override
    public List<TourTypeListResponse> findAllFavByUser(UUID userId, TourSortOption sort) {
        List<Tour> tours = tourRepositoryAdapter.findAllCreatedAndFavoritesBy(userId);
        Map<UUID, TourComputedAttributesDto> attributesByTourId = getComputedAttributesByTourId(userId);

        return sortTours(tours, attributesByTourId, sort).stream()
                .map(tour -> TourMapper.toTypeListResponse(tour, getAttributes(tour, attributesByTourId)))
                .toList();
    }

    @Override
    public List<TourTypeListResponse> findAllNotFavByUser(UUID userId, TourSortOption sort) {
        List<Tour> tours = tourRepositoryAdapter.findAllCreatedButNotFavoritesBy(userId);
        Map<UUID, TourComputedAttributesDto> attributesByTourId = getComputedAttributesByTourId(userId);

        return sortTours(tours, attributesByTourId, sort).stream()
                .map(tour -> TourMapper.toTypeListResponse(tour, getAttributes(tour, attributesByTourId)))
                .toList();
    }

    @Override
    public TourDto findByIdAndUser(UUID id, UUID userId) {
        Tour tour = tourRepositoryAdapter.findByIdAndCreatedBy(id, userId)
                .orElseThrow(() -> new TourNotFoundException("Tour not found or access denied"));

        return TourMapper.toResponse(tour, computeAttributes(tour.getId(), userId));
    }

    @Override
    public TourWithFavoriteDto findByIdAndUserWithFavorite(UUID tourId, UUID userId) {
        Tour tour = tourRepositoryAdapter.findById(tourId)
                .orElseThrow(() -> new TourNotFoundException("Tour not found"));

        boolean isFavorite = userFavoriteTourRepositoryAdapter.existsByUserIdAndTourId(userId, tourId);

        return TourMapper.toWithFavoriteResponse(tour, computeAttributes(tour.getId(), userId), isFavorite);
    }

    @Override
    public TourDto update(UUID id, UUID userId, TourDto tourDto) {
        Tour tour = tourRepositoryAdapter.findByIdAndCreatedBy(id, userId)
                .orElseThrow(() -> new TourNotFoundException("Tour not found or access denied"));

        boolean routeRelevantChanged = !Objects.equals(tour.getTourType(), tourDto.getTourType()) ||
                        !Objects.equals(tour.getStartName(), tourDto.getStartName()) ||
                        !Objects.equals(tour.getEndName(), tourDto.getEndName());

        TourMapper.updateEntity(tourDto, tour);
        tourRepositoryAdapter.save(tour);

        if (routeRelevantChanged) {
            createOrUpdateRoute(tour);
        }

        return TourMapper.toResponse(tour);
    }

    @Override
    @Transactional
    public void deleteByUser(UUID id, UUID userId) {
        if (tourRepositoryAdapter.findByIdAndCreatedBy(id, userId).isEmpty()) {
            throw new TourNotFoundException("Tour not found or access denied");
        }
        tourRepositoryAdapter.deleteByIdAndCreatedBy(id, userId);
    }

    private void createOrUpdateRoute(Tour tour) {

        var routeResponse = routeClient.calculateRoute(
                tour.getStartName(),
                tour.getEndName(),
                String.valueOf(tour.getTourType())
        );

        Route route = routeRepositoryAdapter.findByTourId(tour.getId())
                .orElseGet(() -> {
                    Route r = new Route();
                    r.setId(UUID.randomUUID());
                    r.setTourId(tour.getId());
                    return r;
                });

        route.setDistance(routeResponse.distance());
        route.setDuration(routeResponse.duration());
        route.setCoordinates(routeResponse.coordinates());

        routeRepositoryAdapter.save(route);
    }

    private Map<UUID, TourComputedAttributesDto> getComputedAttributesByTourId(UUID userId) {
        return tourLogRepositoryAdapter.findAllByCreatedBy(userId).stream()
                .collect(Collectors.groupingBy(
                        TourLog::getTourId,
                        Collectors.collectingAndThen(Collectors.toList(), this::computeAttributes)
                ));
    }

    private TourComputedAttributesDto computeAttributes(UUID tourId, UUID userId) {
        return computeAttributes(tourLogRepositoryAdapter.findAllByTourIdAndCreatedBy(tourId, userId));
    }

    private TourComputedAttributesDto computeAttributes(List<TourLog> logs) {
        if (logs.isEmpty()) {
            return TourComputedAttributesDto.empty();
        }

        int logCount = logs.size();
        double averageDifficulty = average(logs, TourLog::getDifficulty);
        double averageTotalDistance = average(logs, TourLog::getTotalDistance);
        double averageTotalTime = average(logs, TourLog::getTotalTime);

        return TourComputedAttributesDto.builder()
                .logCount(logCount)
                .averageDifficulty(roundOneDecimal(averageDifficulty))
                .averageTotalDistance(roundOneDecimal(averageTotalDistance))
                .averageTotalTime(roundOneDecimal(averageTotalTime))
                .popularity(toPopularity(logCount))
                .childFriendliness(toChildFriendliness(averageDifficulty, averageTotalTime, averageTotalDistance))
                .build();
    }

    private double average(List<TourLog> logs, Function<TourLog, Integer> valueExtractor) {
        return logs.stream()
                .map(valueExtractor)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);
    }

    private double roundOneDecimal(double value) {
        return Math.round(value * 10) / 10.0;
    }

    private Popularity toPopularity(int logCount) {
        if (logCount >= 6) {
            return Popularity.VERY_POPULAR;
        }
        if (logCount >= 3) {
            return Popularity.POPULAR;
        }
        if (logCount >= 1) {
            return Popularity.LOW;
        }
        return Popularity.UNKNOWN;
    }

    private ChildFriendliness toChildFriendliness(
            double averageDifficulty,
            double averageTotalTime,
            double averageTotalDistance
    ) {
        if (averageDifficulty <= 1.5 && averageTotalTime <= 90 && averageTotalDistance <= 5) {
            return ChildFriendliness.VERY_CHILD_FRIENDLY;
        }
        if (averageDifficulty <= 2 && averageTotalTime <= 120 && averageTotalDistance <= 8) {
            return ChildFriendliness.CHILD_FRIENDLY;
        }
        if (averageDifficulty <= 3 && averageTotalTime <= 180 && averageTotalDistance <= 15) {
            return ChildFriendliness.MODERATE;
        }
        return ChildFriendliness.NOT_CHILD_FRIENDLY;
    }

    private List<Tour> sortTours(
            List<Tour> tours,
            Map<UUID, TourComputedAttributesDto> attributesByTourId,
            TourSortOption sort
    ) {
        return tours.stream()
                .sorted(getComparator(attributesByTourId, sort))
                .toList();
    }

    private Comparator<Tour> getComparator(
            Map<UUID, TourComputedAttributesDto> attributesByTourId,
            TourSortOption sort
    ) {
        TourSortOption resolvedSort = sort == null ? TourSortOption.POPULARITY_DESC : sort;

        Comparator<Tour> byName = Comparator.comparing(
                Tour::getName,
                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
        );

        Comparator<Tour> byNewest = Comparator.comparing(Tour::getId);
        Comparator<Tour> byPopularity = Comparator
                .comparingInt((Tour tour) -> getAttributes(tour, attributesByTourId).getPopularity().ordinal())
                .thenComparingInt((Tour tour) -> getAttributes(tour, attributesByTourId).getLogCount());
        Comparator<Tour> byChildFriendliness = Comparator
                .comparingInt((Tour tour) -> getAttributes(tour, attributesByTourId).getChildFriendliness().ordinal());

        return switch (resolvedSort) {
            case POPULARITY_ASC -> byPopularity.thenComparing(byName);
            case POPULARITY_DESC -> byPopularity.reversed().thenComparing(byName);
            case CHILD_FRIENDLINESS_ASC -> byChildFriendliness.thenComparing(byName);
            case CHILD_FRIENDLINESS_DESC -> byChildFriendliness.reversed().thenComparing(byName);
            case NAME_ASC -> byName;
            case NAME_DESC -> byName.reversed();
            case NEWEST_FIRST -> byNewest.reversed().thenComparing(byName);
            case OLDEST_FIRST -> byNewest.thenComparing(byName);
        };
    }

    private TourComputedAttributesDto getAttributes(
            Tour tour,
            Map<UUID, TourComputedAttributesDto> attributesByTourId
    ) {
        return attributesByTourId.getOrDefault(tour.getId(), TourComputedAttributesDto.empty());
    }
}
