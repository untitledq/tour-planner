package at.fhtw.swen.tourplanner.controller.request;

import java.time.Instant;

public record TourLogUpdateRequest(
        Instant dateTime,
        Integer rating,
        Integer difficulty,
        Integer totalTime,
        String comment
) {}