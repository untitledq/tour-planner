package at.fhtw.swen.tourplanner.controller.request;

public record TourUpdateRequest(
        String name,
        String description,
        String startName,
        String endName
) {}