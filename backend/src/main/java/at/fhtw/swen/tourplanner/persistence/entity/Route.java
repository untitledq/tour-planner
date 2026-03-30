package at.fhtw.swen.tourplanner.persistence.entity;

import at.fhtw.swen.tourplanner.util.CoordinatesJsonConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "routes")
public class Route {

    @Id
    private UUID id;

    @Column(name = "tour_id", nullable = false, unique = true)
    private UUID tourId;

    @Column(name = "distance")
    private Integer distance;   // meters

    @Column(name = "duration")
    private Integer duration;   // seconds

    @Column(name = "coordinates", columnDefinition = "jsonb")
    @Convert(converter = CoordinatesJsonConverter.class)
    private List<List<Double>> coordinates;

    public Route(UUID id,
                 UUID tourId,
                 Integer distance,
                 Integer duration,
                 List<List<Double>> coordinates) {

        this.id = id;
        this.tourId = tourId;
        this.distance = distance;
        this.duration = duration;
        this.coordinates = coordinates;
    }
}