package at.fhtw.swen.tourplanner.controller.response;

import lombok.Getter;

@Getter
public class TourWithFavoriteResponse extends TourResponse {
    private final boolean favorite;

    public TourWithFavoriteResponse(
            String id,
            String name,
            String description,
            String imagePath,
            String transportType,
            String startName,
            String endName,
            boolean favorite
    ) {
        super(id, name, description, imagePath, transportType, startName, endName);
        this.favorite = favorite;
    }
}