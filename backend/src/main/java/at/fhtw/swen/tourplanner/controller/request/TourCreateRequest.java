package at.fhtw.swen.tourplanner.controller.request;

import at.fhtw.swen.tourplanner.persistence.entity.TourType;

public record TourCreateRequest(
        String name,
        String description,
        String imagePath,
        TourType tourType,
        String startName,
        String endName
) {
    public TourType TourType() {
        return tourType;
    }
}