package it.unibs.ingsw.destinazioni.adapters.jpa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "area_of_interest", schema = "destinazioni")
public class AreaOfInterestEntity {
    @Id
    @Size(max = 100)
    @Column(name = "town", nullable = false, length = 100)
    private String town;

}