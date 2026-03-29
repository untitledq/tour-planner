package at.fhtw.swen.tourplanner.controller.response;

import lombok.Getter;

import java.util.List;

@Getter
public class RouteResponse {
    private final String transportType;
    private final int distance; // meters
    private final int duration; // seconds
    private final List<List<Double>> coordinates; // [[lat,lng],[lat,lng],...]

    public RouteResponse(String transportType, int distance, int duration, List<List<Double>> coordinates) {
        this.transportType = transportType;
        this.distance = distance;
        this.duration = duration;
        this.coordinates = coordinates;
    }
}