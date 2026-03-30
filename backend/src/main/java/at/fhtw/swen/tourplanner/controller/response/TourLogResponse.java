package at.fhtw.swen.tourplanner.controller.response;

import java.time.Instant;
import java.util.UUID;

public record TourLogResponse(
        UUID id,
        UUID tourId,
        Instant dateTime,
        Integer rating,
        Integer difficulty,
        Integer totalTime,
        String comment
) {}