package it.unibs.ingsw.destinazioni.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "visit_days", schema = "destinazioni")
public class VisitDay {
    @EmbeddedId
    private VisitDayId id;

    @MapsId("visitTypeId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "visit_type_id", nullable = false)
    private it.unibs.ingsw.destinazioni.entity.VisitType visitType;

}