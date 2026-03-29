package at.fhtw.swen.tourplanner.controller.request;

import java.time.Instant;

public record TourLogUpdateRequest(
        String comment,
        Integer rating,
        Instant dateTime
) {}