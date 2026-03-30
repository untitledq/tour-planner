package at.fhtw.swen.tourplanner.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Entity
@Table(name = "tours")
public class Tour {

    @Id
    private UUID id;

    @Column(name = "created_by")
    private UUID createdBy;

    @Setter
    private String name;
    @Setter
    private String description;

    @Setter
    @Column(name = "start_name")
    private String startName;

    @Setter
    @Column(name = "end_name")
    private String endName;

    protected Tour() {}

    public Tour(UUID id,
                UUID createdBy,
                String name,
                String description,
                String transportType,
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