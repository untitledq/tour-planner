package at.fhtw.swen.tourplanner.service;

import at.fhtw.swen.tourplanner.controller.request.TourLogCreateRequest;
import at.fhtw.swen.tourplanner.controller.request.TourLogUpdateRequest;
import at.fhtw.swen.tourplanner.controller.response.TourLogListResponse;
import at.fhtw.swen.tourplanner.controller.response.TourLogResponse;

import java.util.List;
import java.util.UUID;

public interface TourLogService {

    List<TourLogListResponse> findAllByUser(UUID userId);

    TourLogResponse findByIdAndUser(UUID id, UUID userId);

    TourLogResponse create(TourLogCreateRequest req, UUID userId);

    TourLogResponse update(UUID id, TourLogUpdateRequest req, UUID userId);

    void deleteByUser(UUID id, UUID userId);
}
