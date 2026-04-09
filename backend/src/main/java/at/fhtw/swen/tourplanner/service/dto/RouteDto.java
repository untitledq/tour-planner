package at.fhtw.swen.tourplanner.service.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class RouteDto {
    private final int distance; // meters
    private final int duration; // seconds
    private final List<List<Double>> coordinates; // [[lat,lng],[lat,lng],...]

    public RouteDto(int distance, int duration, List<List<Double>> coordinates) {
        this.distance = distance;
        this.duration = duration;
        this.coordinates = coordinates;
    }
}