package it.unibs.ingsw.destinazioni.adapters.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "visitors", schema = "destinazioni")
public class VisitorEntity {
    @EmbeddedId
    private VisitorIdEntity id;


    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "visits_id", nullable = false)
    @MapsId("visitId")
    private VisitEntity visitEntity;


    @ManyToOne
    @JoinColumn(name = "users_id", nullable = false)
    @MapsId("visitorId")
    private UserEntity visitorId;

}