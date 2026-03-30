package at.fhtw.swen.tourplanner.service.impl;

import at.fhtw.swen.tourplanner.controller.request.TourLogCreateRequest;
import at.fhtw.swen.tourplanner.controller.request.TourLogUpdateRequest;
import at.fhtw.swen.tourplanner.controller.response.TourLogListResponse;
import at.fhtw.swen.tourplanner.controller.response.TourLogResponse;
import at.fhtw.swen.tourplanner.mapper.TourLogMapper;
import at.fhtw.swen.tourplanner.persistence.entity.TourLog;
import at.fhtw.swen.tourplanner.persistence.repository.TourLogJpaRepository;
import at.fhtw.swen.tourplanner.service.TourLogService;
import at.fhtw.swen.tourplanner.util.UUIDv7;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TourLogServiceImpl implements TourLogService {

    private final TourLogJpaRepository repository;

    public TourLogServiceImpl(TourLogJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<TourLogListResponse> findAllByUser(UUID userId) {
        List<TourLog> logs = repository.findAllByCreatedBy(userId);
        return TourLogMapper.toListResponseList(logs);
    }

    @Override
    public TourLogResponse findByIdAndUser(UUID id, UUID userId) {
        TourLog entity = repository.findByIdAndCreatedBy(id, userId)
                .orElseThrow(() -> new RuntimeException("Tour Log not found or access denied"));

        return TourLogMapper.toResponse(entity);
    }

    @Override
    public TourLogResponse create(TourLogCreateRequest req, UUID userId) {
        TourLog entity = TourLogMapper.fromCreateRequest(
                UUIDv7.randomUUID(),
                userId,
                req
        );

        repository.save(entity);
        return TourLogMapper.toResponse(entity);
    }

    @Override
    public TourLogResponse update(UUID id, TourLogUpdateRequest req, UUID userId) {
        TourLog entity = repository.findByIdAndCreatedBy(id, userId)
                .orElseThrow(() -> new RuntimeException("Tour Log not found or access denied"));

        TourLogMapper.updateEntity(req, entity);
        return TourLogMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public void deleteByUser(UUID id, UUID userId) {
        if (repository.findByIdAndCreatedBy(id, userId).isEmpty()) {
            throw new RuntimeException("Tour Log not found or access denied");
        }
        repository.deleteByIdAndCreatedBy(id, userId);
    }
}