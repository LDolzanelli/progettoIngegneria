package it.unibs.ingsw.destinazioni.adapters.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "visit_days", schema = "destinazioni")
public class VisitDayEntity {
    @EmbeddedId
    private VisitDayIdEntity id;

    @MapsId("visitTypeId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "visit_type_id", nullable = false)
    private VisitTypeEntity visitTypeEntity;

}