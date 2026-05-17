package at.fhtw.swen.tourplanner.service.dto;

import at.fhtw.swen.tourplanner.persistence.entity.TourType;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourDto {

    private UUID id;
    private UUID createdBy;

    private String name;
    private String description;
    private String imagePath;

    private TourType tourType;

    private String startName;
    private String endName;

    private TourComputedAttributesDto computedAttributes;

    public TourDto(String id, String name, String description, String imagePath, TourType tourType, String startName, String endName) {
        this(id, name, description, imagePath, tourType, startName, endName, TourComputedAttributesDto.empty());
    }

    public TourDto(String id,
                   String name,
                   String description,
                   String imagePath,
                   TourType tourType,
                   String startName,
                   String endName,
                   TourComputedAttributesDto computedAttributes) {
        this.id = UUID.fromString(id);
        this.createdBy = UUID.fromString(id);
        this.name = name;
        this.description = description;
        this.imagePath = imagePath;
        this.tourType = tourType;
        this.startName = startName;
        this.endName = endName;
        this.computedAttributes = computedAttributes;
    }
}
