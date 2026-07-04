package at.fhtw.swen.tourplanner.controller.request;

import at.fhtw.swen.tourplanner.persistence.entity.TourType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourRequest {

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
}
