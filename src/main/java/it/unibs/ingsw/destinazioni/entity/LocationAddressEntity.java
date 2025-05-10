package it.unibs.ingsw.destinazioni.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "location_addresses", schema = "destinazioni")
public class LocationAddressEntity {
    @EmbeddedId
    private LocationAddressIdEntity id;

    @MapsId("town")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "town", nullable = false)
    private AreaOfInterestEntity town;

    @Size(max = 50)
    @NotNull
    @Column(name = "province", nullable = false, length = 50)
    private String province;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "location_id", nullable = false)
    private LocationEntity location;

}