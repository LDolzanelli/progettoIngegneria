package it.unibs.ingsw.destinazioni.adapters.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "volunteer_available_dates")
public class VolunteerAvailableDateEntity {
    @EmbeddedId
    private VolunteerAvailableDateEntityId id;

    @MapsId("volunteerId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "volunteer_id", nullable = false)
    private UserEntity volunteer;

}