package it.unibs.ingsw.destinazioni.adapters.jpa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "area_of_interest", schema = "destinazioni")
public class AreaOfInterestEntity {
    @Id
    @Size(max = 100)
    @Column(name = "town", nullable = false, length = 100)
    private String town;

    @OneToMany(mappedBy = "town")
    private Set<LocationAddressEntity> locationAddressEntities = new LinkedHashSet<>();

}