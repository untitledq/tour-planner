package at.fhtw.swen.tourplanner.persistence.repository;

public record SearchResultRow(
        String type,
        String id,
        String tourId,
        String title,
        String snippet
) {
}
