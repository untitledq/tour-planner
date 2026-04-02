package at.fhtw.swen.tourplanner.controller.request;

import at.fhtw.swen.tourplanner.persistence.entity.TourType;

public record TourUpdateRequest(
        String name,
        String description,
        String imagePath,
        TourType tourType,
        String startName,
        String endName
) {}