package at.fhtw.swen.tourplanner.service;

import at.fhtw.swen.tourplanner.service.dto.TourDto;
import at.fhtw.swen.tourplanner.service.dto.export.TourExportDto;

import java.util.UUID;

public interface TourExportService {

    String exportTour(UUID tourId);
    TourDto importTour(UUID userId, TourExportDto dto);
}