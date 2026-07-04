package at.fhtw.swen.tourplanner.service.dto;

import at.fhtw.swen.tourplanner.persistence.entity.TourType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourDto {

    @NotNull(message = "ID cannot be null")
    private UUID id;

    @NotNull(message = "CreatedBy cannot be null")
    private UUID createdBy;

    @NotBlank(message = "Name is mandatory")
    @Size(max = 100, message = "Name must be less than or equal to 100 characters")
    private String name;

    @Size(max = 500, message = "Description must be less than or equal to 500 characters")
    private String description;

    @Size(max = 255, message = "ImagePath must be less than or equal to 255 characters")
    private String imagePath;

    @NotNull(message = "TourType cannot be null")
    private TourType tourType;

    @NotBlank(message = "StartName is mandatory")
    @Size(max = 100, message = "StartName must be less than or equal to 100 characters")
    private String startName;

    @NotBlank(message = "EndName is mandatory")
    @Size(max = 100, message = "EndName must be less than or equal to 100 characters")
    private String endName;

    private TourComputedAttributesDto computedAttributes;

    public TourDto(String id, String name, String description, String imagePath, TourType tourType, String startName, String endName) {
        this.id = UUID.fromString(id);
        this.createdBy = UUID.fromString(id);
        this.name = name;
        this.description = description;
        this.imagePath = imagePath;
        this.tourType = tourType;
        this.startName = startName;
        this.endName = endName;
        this.computedAttributes = TourComputedAttributesDto.empty();
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