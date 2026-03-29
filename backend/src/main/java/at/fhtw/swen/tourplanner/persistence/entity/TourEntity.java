package at.fhtw.swen.tourplanner.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.util.UUID;

@Getter
@Entity
@Table(name = "tours")
public class TourEntity {

    @Id
    private UUID id;

    @Column(name = "created_by")
    private UUID createdBy;

    private String name;
    private String description;

    @Column(name = "start_name")
    private String startName;

    @Column(name = "end_name")
    private String endName;

    protected TourEntity() {}

    public TourEntity(UUID id,
                      UUID createdBy,
                      String name,
                      String description,
                      String startName,
                      String endName) {
        this.id = id;
        this.createdBy = createdBy;
        this.name = name;
        this.description = description;
        this.startName = startName;
        this.endName = endName;
    }
}