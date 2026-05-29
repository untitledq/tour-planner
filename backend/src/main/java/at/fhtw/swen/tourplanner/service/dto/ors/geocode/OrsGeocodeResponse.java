package at.fhtw.swen.tourplanner.service.dto.ors.geocode;

import java.util.List;

public record OrsGeocodeResponse(
        List<OrsFeature> features
) {
}