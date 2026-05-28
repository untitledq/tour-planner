package at.fhtw.swen.tourplanner.service.dto.ors.route;

import java.util.List;

public record OrsRouteResponse(
        List<OrsRoute> routes
) {
}