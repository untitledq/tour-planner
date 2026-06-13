package at.fhtw.swen.tourplanner.service.impl;

import at.fhtw.swen.tourplanner.persistence.repository.adapter.TourLogRepositoryAdapter;
import at.fhtw.swen.tourplanner.persistence.entity.TourLog;
import at.fhtw.swen.tourplanner.service.dto.TourLogDto;
import at.fhtw.swen.tourplanner.service.mapper.TourLogMapper;
import at.fhtw.swen.tourplanner.service.TourLogService;
import at.fhtw.swen.tourplanner.util.UUIDv7;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TourLogServiceImpl implements TourLogService {

    private final TourLogRepositoryAdapter tourLogRepositoryAdapter;

    public TourLogServiceImpl(TourLogRepositoryAdapter tourLogRepositoryAdapter) {
        this.tourLogRepositoryAdapter = tourLogRepositoryAdapter;
    }

    @Override
    public List<TourLogDto> findAllByUser(UUID userId) {
        List<TourLog> logs = tourLogRepositoryAdapter.findAllByCreatedBy(userId);
        return TourLogMapper.toListResponseList(logs);
    }

    @Override
    public TourLogDto findByIdAndUser(UUID id, UUID userId) {
        TourLog entity = tourLogRepositoryAdapter.findByIdAndCreatedBy(id, userId);
        return TourLogMapper.toResponse(entity);
    }

    @Override
    public List<TourLogDto> findAllByTourAndUser(UUID tourId, UUID userId) {
        List<TourLog> logs = tourLogRepositoryAdapter.findAllByTourIdAndCreatedBy(tourId, userId);
        return TourLogMapper.toListResponseList(logs);
    }

    @Override
    public TourLogDto create(TourLogDto req, UUID userId) {
        TourLog entity = TourLogMapper.fromCreateRequest(
                UUIDv7.randomUUID(),
                userId,
                req
        );

        tourLogRepositoryAdapter.save(entity);
        return TourLogMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public TourLogDto update(UUID id, TourLogDto req, UUID userId) {
        TourLog entity = tourLogRepositoryAdapter.findByIdAndCreatedBy(id, userId);
        TourLogMapper.updateEntity(req, entity);
        return TourLogMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public void deleteByUser(UUID id, UUID userId) {
        tourLogRepositoryAdapter.deleteByIdAndCreatedBy(id, userId);
    }
}