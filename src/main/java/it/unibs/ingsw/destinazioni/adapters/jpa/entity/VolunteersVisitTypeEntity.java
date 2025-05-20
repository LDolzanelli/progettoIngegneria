package it.unibs.ingsw.destinazioni.adapters.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "volunteers_visit_types")
public class VolunteersVisitTypeEntity {
    @EmbeddedId
    private VolunteersVisitTypeEntityId id;

    @MapsId("visitTypeId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "visit_type_id", nullable = false)
    private VisitTypeEntity visitType;

    @MapsId("volunteerId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "volunteer_id", nullable = false)
    private UserEntity volunteer;

}