package at.fhtw.swen.tourplanner.controller.response;

import at.fhtw.swen.tourplanner.service.dto.TourComputedAttributesDto;

public record TourListResponse(
        String id,
        String name,
        TourComputedAttributesDto computedAttributes
) {}
