package at.fhtw.swen.tourplanner.controller.request;

import java.time.Instant;
import java.util.UUID;

public record TourLogCreateRequest(
        UUID tourId,
        Instant dateTime,
        Integer rating,
        String comment
) {}