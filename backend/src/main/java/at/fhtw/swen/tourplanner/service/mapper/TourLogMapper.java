package at.fhtw.swen.tourplanner.service.mapper;

import at.fhtw.swen.tourplanner.controller.request.TourLogCreateRequest;
import at.fhtw.swen.tourplanner.controller.request.TourLogUpdateRequest;
import at.fhtw.swen.tourplanner.controller.response.TourLogListResponse;
import at.fhtw.swen.tourplanner.controller.response.TourLogResponse;
import at.fhtw.swen.tourplanner.persistence.entity.TourLog;

import java.util.List;
import java.util.UUID;

public class TourLogMapper {

    public static TourLog fromCreateRequest(UUID id, UUID userId, TourLogCreateRequest req) {
        return new TourLog(
                id,
                req.tourId(),
                userId,
                req.dateTime(),
                req.rating(),
                req.comment()
        );
    }

    public static void updateEntity(TourLogUpdateRequest req, TourLog entity) {
        entity.setDateTime(req.dateTime());
        entity.setRating(req.rating());
        entity.setComment(req.comment());
    }

    public static TourLogResponse toResponse(TourLog entity) {
        return new TourLogResponse(
                entity.getId(),
                entity.getTourId(),
                entity.getDateTime(),
                entity.getRating(),
                entity.getComment()
        );
    }

    public static TourLogListResponse toListResponse(TourLog entity) {
        return new TourLogListResponse(
                entity.getId().toString(),
                entity.getRating()
        );
    }

    public static List<TourLogResponse> toResponseList(List<TourLog> entities) {
        return entities.stream().map(TourLogMapper::toResponse).toList();
    }

    public static List<TourLogListResponse> toListResponseList(List<TourLog> entities) {
        return entities.stream().map(TourLogMapper::toListResponse).toList();
    }
}
