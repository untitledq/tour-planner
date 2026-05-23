package at.fhtw.swen.tourplanner.service.dto;

import java.util.List;

public record OrsRouteResponse(
        List<OrsRoute> routes
) {
}