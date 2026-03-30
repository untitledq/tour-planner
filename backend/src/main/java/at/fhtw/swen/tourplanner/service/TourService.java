package at.fhtw.swen.tourplanner.service;

import at.fhtw.swen.tourplanner.controller.request.TourCreateRequest;
import at.fhtw.swen.tourplanner.controller.request.TourUpdateRequest;
import at.fhtw.swen.tourplanner.controller.response.TourListResponse;
import at.fhtw.swen.tourplanner.controller.response.TourResponse;

import java.util.List;
import java.util.UUID;

public interface TourService {
    TourResponse create(UUID userId, TourCreateRequest req, UUID id);

    List<TourListResponse> findAllByUser(UUID userId);

    TourResponse findByIdAndUser(UUID id, UUID userId);

    TourResponse update(UUID id, UUID userId, TourUpdateRequest req);

    void deleteByUser(UUID id, UUID userId);
}
