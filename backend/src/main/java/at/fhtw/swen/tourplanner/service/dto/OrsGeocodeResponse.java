package at.fhtw.swen.tourplanner.service.dto;

import java.util.List;

public record OrsGeocodeResponse(
        List<OrsFeature> features
) {
}