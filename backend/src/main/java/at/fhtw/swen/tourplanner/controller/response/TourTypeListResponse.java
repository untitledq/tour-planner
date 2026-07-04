package at.fhtw.swen.tourplanner.controller.response;

import at.fhtw.swen.tourplanner.persistence.entity.TourType;
import at.fhtw.swen.tourplanner.service.dto.TourComputedAttributesDto;

public record TourTypeListResponse (
        String id,
        String name,
        TourType tourType,
        TourComputedAttributesDto computedAttributes
) {}
