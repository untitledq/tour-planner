package at.fhtw.swen.tourplanner.controller.response;

import at.fhtw.swen.tourplanner.controller.response.enums.SearchResultType;

public record SearchResultResponse(
        SearchResultType type,
        String id,
        String tourId,
        String title,
        String snippet
) {
}
