package at.fhtw.swen.tourplanner.controller.response;

import at.fhtw.swen.tourplanner.persistence.entity.TourType;
import lombok.Getter;

@Getter
public class TourWithFavoriteResponse extends TourResponse {
    private final boolean favorite;

    public TourWithFavoriteResponse(
            String id,
            String name,
            String description,
            String imagePath,
            TourType tourType,
            String startName,
            String endName,
            boolean favorite
    ) {
        super(id, name, description, imagePath, tourType, startName, endName);
        this.favorite = favorite;
    }
}