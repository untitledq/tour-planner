package at.fhtw.swen.tourplanner.service.impl;

import at.fhtw.swen.tourplanner.controller.request.TourCreateRequest;
import at.fhtw.swen.tourplanner.controller.request.TourUpdateRequest;
import at.fhtw.swen.tourplanner.controller.response.TourListResponse;
import at.fhtw.swen.tourplanner.controller.response.TourResponse;
import at.fhtw.swen.tourplanner.mapper.TourMapper;
import at.fhtw.swen.tourplanner.persistence.entity.Tour;
import at.fhtw.swen.tourplanner.persistence.repository.TourJpaRepository;
import at.fhtw.swen.tourplanner.service.TourService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TourServiceImpl implements TourService {

    private final TourJpaRepository repository;

    public TourServiceImpl(TourJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public TourResponse create(UUID userId, TourCreateRequest req, UUID id) {
        Tour entity = TourMapper.fromCreateRequest(id, userId, req);
        repository.save(entity);
        return TourMapper.toResponse(entity);
    }

    @Override
    public List<TourListResponse> findAllByUser(UUID userId) {
        List<Tour> tours = repository.findAllByCreatedBy(userId);
        return TourMapper.toListResponseList(tours);
    }

    @Override
    public TourResponse findByIdAndUser(UUID id, UUID userId) {
        Tour tour = repository.findByIdAndCreatedBy(id, userId)
                .orElseThrow(() -> new RuntimeException("Tour not found or access denied"));

        return TourMapper.toResponse(tour);
    }

    @Override
    public TourResponse update(UUID id, UUID userId, TourUpdateRequest req) {
        Tour tour = repository.findByIdAndCreatedBy(id, userId)
                .orElseThrow(() -> new RuntimeException("Tour not found or access denied"));

        TourMapper.updateEntity(req, tour);

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
}