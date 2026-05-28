package at.fhtw.swen.tourplanner.service.dto.export;

import at.fhtw.swen.tourplanner.persistence.entity.TourType;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@JacksonXmlRootElement(localName = "tour")
public record TourExportDto(

        @JacksonXmlProperty(localName = "exportedAt")
        Instant exportedAt,

        UUID id,
        UUID createdBy,
        String name,
        String description,
        String imagePath,
        TourType tourType,
        String startName,
        String endName,

        @JacksonXmlElementWrapper(localName = "logs")
        @JacksonXmlProperty(localName = "log")
        @JsonProperty("logs") List<TourLogExportDto> logs
) {}