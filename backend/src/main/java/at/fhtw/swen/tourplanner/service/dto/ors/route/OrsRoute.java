package at.fhtw.swen.tourplanner.service.dto.ors.route;

public record OrsRoute(
        OrsSummary summary,
        String geometry
) {
}