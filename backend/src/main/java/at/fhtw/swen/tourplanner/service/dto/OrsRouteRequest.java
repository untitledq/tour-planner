package at.fhtw.swen.tourplanner.service.dto;

import java.util.List;

public record OrsRouteRequest(
        List<List<Double>> coordinates
) {
}