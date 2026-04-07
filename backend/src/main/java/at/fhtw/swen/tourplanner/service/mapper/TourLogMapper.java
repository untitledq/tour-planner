package at.fhtw.swen.tourplanner.service.mapper;

import at.fhtw.swen.tourplanner.service.dto.TourLogDto;
import at.fhtw.swen.tourplanner.persistence.entity.TourLog;

import java.util.List;
import java.util.UUID;

public class TourLogMapper {

    public static TourLog fromCreateRequest(UUID id, UUID userId, TourLogDto tourLogDto) {
        return new TourLog(
                id,
                tourLogDto.getTourId(),
                userId,
                tourLogDto.getDateTime(),
                tourLogDto.getRating(),
                tourLogDto.getDifficulty(),
                tourLogDto.getTotalDistance(),
                tourLogDto.getTotalTime(),
                tourLogDto.getComment()
        );
    }

    public static void updateEntity(TourLogDto tourLogDto, TourLog entity) {
        entity.setDateTime(tourLogDto.getDateTime());
        entity.setRating(tourLogDto.getRating());
        entity.setDifficulty(tourLogDto.getDifficulty());
        entity.setTotalDistance(tourLogDto.getTotalDistance());
        entity.setTotalTime(tourLogDto.getTotalTime());
        entity.setComment(tourLogDto.getComment());
    }

    public static TourLogDto toResponse(TourLog entity) {
        return new TourLogDto(
                entity.getId(),
                entity.getTourId(),
                entity.getDateTime(),
                entity.getRating(),
                entity.getDifficulty(),
                entity.getTotalDistance(),
                entity.getTotalTime(),
                entity.getComment()
        );
    }

    public static TourLogDto toListResponse(TourLog entity) {
        return new TourLogDto(
                entity.getId(),
                entity.getRating()
        );
    }

    public static List<TourLogDto> toListResponseList(List<TourLog> entities) {
        return entities.stream().map(TourLogMapper::toListResponse).toList();
    }
}
