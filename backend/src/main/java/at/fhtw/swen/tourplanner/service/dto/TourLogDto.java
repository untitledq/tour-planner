package at.fhtw.swen.tourplanner.service.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourLogDto {

    @NotNull(message = "ID cannot be null")
    private UUID id;

    @NotNull(message = "TourId cannot be null")
    private UUID tourId;

    @NotNull(message = "DateTime cannot be null")
    @PastOrPresent(message = "DateTime must be in the past or present")
    private Instant dateTime;

    @NotNull(message = "Rating cannot be null")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    @NotNull(message = "Difficulty cannot be null")
    @Min(value = 1, message = "Difficulty must be at least 1")
    @Max(value = 5, message = "Difficulty must be at most 5")
    private Integer difficulty;

    @NotNull(message = "TotalDistance cannot be null")
    @Positive(message = "TotalDistance must be positive")
    private Integer totalDistance;

    @NotNull(message = "TotalTime cannot be null")
    @Positive(message = "TotalTime must be positive")
    private Integer totalTime;

    @Size(max = 500, message = "Comment must be less than or equal to 500 characters")
    private String comment;
}