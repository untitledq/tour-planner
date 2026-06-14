package at.fhtw.swen.tourplanner.service.impl;

import at.fhtw.swen.tourplanner.persistence.entity.Tour;
import at.fhtw.swen.tourplanner.persistence.entity.TourLog;
import at.fhtw.swen.tourplanner.controller.exception.TourNotFoundException;
import at.fhtw.swen.tourplanner.persistence.repository.adapter.TourLogRepositoryAdapter;
import at.fhtw.swen.tourplanner.persistence.repository.adapter.TourRepositoryAdapter;
import at.fhtw.swen.tourplanner.service.TourExportService;
import at.fhtw.swen.tourplanner.service.dto.TourDto;
import at.fhtw.swen.tourplanner.service.dto.export.TourExportDto;
import at.fhtw.swen.tourplanner.service.dto.export.TourLogExportDto;
import at.fhtw.swen.tourplanner.controller.exception.TourExportException;
import at.fhtw.swen.tourplanner.controller.exception.TourImportException;
import at.fhtw.swen.tourplanner.util.LoggerService;
import at.fhtw.swen.tourplanner.util.UUIDv7;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class TourExportServiceImpl implements TourExportService {

    private final Logger logger =
            LoggerService.getInstance().getLogger(getClass());

    private final TourRepositoryAdapter tourRepositoryAdapter;
    private final TourLogRepositoryAdapter tourLogRepositoryAdapter;
    private final XmlMapper xmlMapper;

    public TourExportServiceImpl(
            TourRepositoryAdapter tourRepositoryAdapter,
            TourLogRepositoryAdapter tourLogRepositoryAdapter
    ) {
        this.tourRepositoryAdapter = tourRepositoryAdapter;
        this.tourLogRepositoryAdapter = tourLogRepositoryAdapter;

        this.xmlMapper = XmlMapper.builder()
                .addModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .enable(com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator.Feature.WRITE_XML_DECLARATION)
                .build();
    }

    @Override
    public String exportTour(UUID tourId) {

        logger.info("Exporting tour with id={}", tourId);

        Tour tour = tourRepositoryAdapter.findById(tourId)
                .orElseThrow(() -> new TourNotFoundException(
                        "Tour not found for export: " + tourId
                ));

        List<TourLogExportDto> logDtos =
                tourLogRepositoryAdapter.findByTourId(tourId)
                        .stream()
                        .map(this::mapTourLogToExportDto)
                        .toList();

        TourExportDto exportDto = new TourExportDto();

        exportDto.setExportedAt(Instant.now());
        exportDto.setId(tour.getId());
        exportDto.setCreatedBy(tour.getCreatedBy());
        exportDto.setName(tour.getName());
        exportDto.setDescription(tour.getDescription());
        exportDto.setImagePath(tour.getImagePath());
        exportDto.setTourType(tour.getTourType());
        exportDto.setStartName(tour.getStartName());
        exportDto.setEndName(tour.getEndName());
        exportDto.setLogs(logDtos);

        try {
            String xml = xmlMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(exportDto);

            logger.info("Successfully exported tour id={}", tourId);

            return xml;

        } catch (JsonProcessingException exception) {

            logger.error(
                    "Failed to serialize tour id={} to XML",
                    tourId,
                    exception
            );

            throw new TourExportException(
                    "Failed to export tour as XML for id: " + tourId,
                    exception
            );
        }
    }

    private TourLogExportDto mapTourLogToExportDto(TourLog log) {
        return new TourLogExportDto(
                log.getId(),
                log.getTourId(),
                log.getCreatedBy(),
                log.getDateTime(),
                log.getRating(),
                log.getDifficulty(),
                log.getTotalDistance(),
                log.getTotalTime(),
                log.getComment()
        );
    }

    @Override
    public TourDto importTour(UUID userId, TourExportDto dto) {

        UUID newTourId = UUIDv7.randomUUID();

        try {
            Tour tour = new Tour(
                    newTourId,
                    userId,
                    dto.getName(),
                    dto.getDescription(),
                    dto.getImagePath(),
                    dto.getTourType(),
                    dto.getStartName(),
                    dto.getEndName()
            );

            tourRepositoryAdapter.save(tour);

            if (dto.getLogs() != null) {
                dto.getLogs().forEach(logDto -> {

                    TourLog log = new TourLog(
                            UUIDv7.randomUUID(),
                            newTourId,
                            userId,
                            logDto.dateTime(),
                            logDto.rating(),
                            logDto.difficulty(),
                            logDto.totalDistance(),
                            logDto.totalTime(),
                            logDto.comment()
                    );

                    tourLogRepositoryAdapter.save(log);
                });
            }

            return new TourDto(
                    tour.getId().toString(),
                    tour.getName(),
                    tour.getDescription(),
                    tour.getImagePath(),
                    tour.getTourType(),
                    tour.getStartName(),
                    tour.getEndName()
            );

        } catch (Exception e) {
            throw new TourImportException(
                    "Failed to import tour: " + dto.getName(),
                    e
            );
        }
    }
}