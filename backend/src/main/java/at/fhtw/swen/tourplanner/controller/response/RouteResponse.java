package at.fhtw.swen.tourplanner.controller.response;

import lombok.Getter;

import java.util.List;

@Getter
public class RouteResponse {
    private final int distance; // meters
    private final int duration; // seconds
    private final List<List<Double>> coordinates; // [[lat,lng],[lat,lng],...]

    public RouteResponse(int distance, int duration, List<List<Double>> coordinates) {
        this.distance = distance;
        this.duration = duration;
        this.coordinates = coordinates;
    }
}