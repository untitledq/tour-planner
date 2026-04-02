package at.fhtw.swen.tourplanner.service.mapper;

import at.fhtw.swen.tourplanner.controller.request.TourCreateRequest;
import at.fhtw.swen.tourplanner.controller.request.TourUpdateRequest;
import at.fhtw.swen.tourplanner.controller.response.TourListResponse;
import at.fhtw.swen.tourplanner.controller.response.TourResponse;
import at.fhtw.swen.tourplanner.controller.response.TourWithFavoriteResponse;
import at.fhtw.swen.tourplanner.persistence.entity.Tour;

import java.util.List;
import java.util.UUID;

public class TourMapper {

    public static Tour fromCreateRequest(UUID id, UUID userId, TourCreateRequest req) {
        return new Tour(
                id,
                userId,
                req.name(),
                req.description(),
                req.imagePath(),
                req.TourType(),
                req.startName(),
                req.endName()
        );
    }

    public static void updateEntity(TourUpdateRequest req, Tour tour) {
        tour.setName(req.name());
        tour.setDescription(req.description());
        tour.setStartName(req.startName());
        tour.setEndName(req.endName());
    }

    public static TourResponse toResponse(Tour tour) {
        return new TourResponse(
                tour.getId().toString(),
                tour.getName(),
                tour.getDescription(),
                tour.getImagePath(),
                tour.getTourType(),
                tour.getStartName(),
                tour.getEndName()
        );
    }

    public static TourWithFavoriteResponse toWithFavoriteResponse(Tour tour, boolean isFavorite) {
        return new TourWithFavoriteResponse(
                tour.getId().toString(),
                tour.getName(),
                tour.getDescription(),
                tour.getImagePath(),
                tour.getTourType(),
                tour.getStartName(),
                tour.getEndName(),
                isFavorite
        );
    }

    public static TourListResponse toListResponse(Tour tour) {
        return new TourListResponse(
                tour.getId().toString(),
                tour.getName()
        );
    }

    public static List<TourListResponse> toListResponseList(List<Tour> tours) {
        return tours.stream().map(TourMapper::toListResponse).toList();
    }
}
