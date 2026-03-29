package at.fhtw.swen.tourplanner.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "tour_logs")
public class TourLogEntity {

    @Id
    private UUID id;

    @Column(name = "tour_id", nullable = false)
    private UUID tourId;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "date_time")
    private Instant dateTime;

    @Column
    private Integer rating;

    @Column
    private String comment;

    protected TourLogEntity() {}

    public TourLogEntity(UUID id,
                         UUID tourId,
                         UUID createdBy,
                         Instant dateTime,
                         Integer rating,
                         String comment) {
        this.id = id;
        this.tourId = tourId;
        this.createdBy = createdBy;
        this.dateTime = dateTime;
        this.rating = rating;
        this.comment = comment;
    }
}