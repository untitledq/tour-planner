package at.fhtw.swen.tourplanner.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourComputedAttributesDto {
    private Integer logCount;
    private Double averageDifficulty;
    private Double averageTotalDistance;
    private Double averageTotalTime;
    private Popularity popularity;
    private ChildFriendliness childFriendliness;

    public static TourComputedAttributesDto empty() {
        return TourComputedAttributesDto.builder()
                .logCount(0)
                .popularity(Popularity.UNKNOWN)
                .childFriendliness(ChildFriendliness.UNKNOWN)
                .build();
    }
}
