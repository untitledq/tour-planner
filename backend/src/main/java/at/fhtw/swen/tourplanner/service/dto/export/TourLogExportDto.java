package at.fhtw.swen.tourplanner.service.dto.export;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.time.Instant;
import java.util.UUID;

@JacksonXmlRootElement(localName = "log")
public record TourLogExportDto(

        UUID id,
        UUID tourId,
        UUID createdBy,
        Instant dateTime,
        Integer rating,
        Integer difficulty,
        Integer totalDistance,
        Integer totalTime,
        String comment
) {
}