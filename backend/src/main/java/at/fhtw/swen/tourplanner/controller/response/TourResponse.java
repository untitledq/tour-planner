package at.fhtw.swen.tourplanner.controller.response;

import lombok.Getter;

@Getter
public class TourResponse {
    private final String id;
    private final String name;
    private final String description;
    private final String imagePath;
    private final String transportType;
    private final String startName;
    private final String endName;

    public TourResponse(String id, String name, String description, String imagePath, String transportType, String startName, String endName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imagePath = imagePath;
        this.transportType = transportType;
        this.startName = startName;
        this.endName = endName;
    }
}