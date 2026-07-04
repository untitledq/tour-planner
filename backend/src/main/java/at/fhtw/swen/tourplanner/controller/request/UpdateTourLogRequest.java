package at.fhtw.swen.tourplanner.controller.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTourLogRequest {

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
