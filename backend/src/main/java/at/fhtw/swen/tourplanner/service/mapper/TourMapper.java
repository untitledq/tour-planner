package at.fhtw.swen.tourplanner.service.mapper;

import at.fhtw.swen.tourplanner.controller.response.TourTypeListResponse;
import at.fhtw.swen.tourplanner.service.dto.TourComputedAttributesDto;
import at.fhtw.swen.tourplanner.service.dto.TourDto;
import at.fhtw.swen.tourplanner.controller.response.TourListResponse;
import at.fhtw.swen.tourplanner.service.dto.TourWithFavoriteDto;
import at.fhtw.swen.tourplanner.persistence.entity.Tour;

import java.util.List;
import java.util.UUID;

public class TourMapper {

    public static Tour fromCreateRequest(UUID id, UUID userId, TourDto tourDto) {
        return new Tour(
                id,
                userId,
                tourDto.getName(),
                tourDto.getDescription(),
                tourDto.getImagePath(),
                tourDto.getTourType(),
                tourDto.getStartName(),
                tourDto.getEndName()
        );
    }

    public static void updateEntity(TourDto tourDto, Tour tour) {
        tour.setName(tourDto.getName());
        tour.setDescription(tourDto.getDescription());
        tour.setTourType(tourDto.getTourType());
        tour.setStartName(tourDto.getStartName());
        tour.setEndName(tourDto.getEndName());
    }

    public static TourDto toResponse(Tour tour) {
        return toResponse(tour, TourComputedAttributesDto.empty());
    }

    public static TourDto toResponse(Tour tour, TourComputedAttributesDto computedAttributes) {
        return new TourDto(
                tour.getId().toString(),
                tour.getName(),
                tour.getDescription(),
                tour.getImagePath(),
                tour.getTourType(),
                tour.getStartName(),
                tour.getEndName(),
                computedAttributes
        );
    }

    public static TourWithFavoriteDto toWithFavoriteResponse(Tour tour, boolean isFavorite) {
        return toWithFavoriteResponse(tour, TourComputedAttributesDto.empty(), isFavorite);
    }

    public static TourWithFavoriteDto toWithFavoriteResponse(
            Tour tour,
            TourComputedAttributesDto computedAttributes,
            boolean isFavorite
    ) {
        return new TourWithFavoriteDto(
                tour.getId().toString(),
                tour.getName(),
                tour.getDescription(),
                tour.getImagePath(),
                tour.getTourType(),
                tour.getStartName(),
                tour.getEndName(),
                computedAttributes,
                isFavorite
        );
    }

    public static TourListResponse toListResponse(Tour tour) {
        return toListResponse(tour, TourComputedAttributesDto.empty());
    }

    public static TourListResponse toListResponse(Tour tour, TourComputedAttributesDto computedAttributes) {
        return new TourListResponse(
                tour.getId().toString(),
                tour.getName(),
                computedAttributes
        );
    }

    public static TourTypeListResponse toTypeListResponse(Tour tour) {
        return toTypeListResponse(tour, TourComputedAttributesDto.empty());
    }

    public static TourTypeListResponse toTypeListResponse(Tour tour, TourComputedAttributesDto computedAttributes) {
        return new TourTypeListResponse(
                tour.getId().toString(),
                tour.getName(),
                tour.getTourType(),
                computedAttributes
        );
    }

    public static List<TourListResponse> toTourResponseList(List<Tour> tours) {
        return tours.stream().map(TourMapper::toListResponse).toList();
    }

    public static List<TourTypeListResponse> toTourTypeResponseList(List<Tour> tours) {
        return tours.stream().map(TourMapper::toTypeListResponse).toList();
    }
}
