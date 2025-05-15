package it.unibs.ingsw.destinazioni.adapters.jpa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "locations", schema = "destinazioni")
public class LocationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 100)
    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Lob
    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "location")
    private Set<LocationAddressEntity> locationAddressEntities = new LinkedHashSet<>();

    @OneToMany(mappedBy = "location")
    private Set<VisitTypeEntity> visitTypeEntities = new LinkedHashSet<>();

}