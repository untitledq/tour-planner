package at.fhtw.swen.tourplanner.controller.request;

public record TourCreateRequest(
        String name,
        String description,
        String imagePath,
        String transportType,
        String startName,
        String endName
) {}