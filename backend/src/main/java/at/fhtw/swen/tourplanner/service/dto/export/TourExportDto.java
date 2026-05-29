package at.fhtw.swen.tourplanner.service.dto.export;

import at.fhtw.swen.tourplanner.persistence.entity.TourType;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@JacksonXmlRootElement(localName = "tour")
public class TourExportDto {
        private Instant exportedAt;
        private UUID id;
        private UUID createdBy;
        private String name;
        private String description;
        private String imagePath;
        private TourType tourType;
        private String startName;
        private String endName;

        @JacksonXmlElementWrapper(localName = "logs")
        @JacksonXmlProperty(localName = "log")
        private List<TourLogExportDto> logs;

        public TourExportDto() {
        }
}