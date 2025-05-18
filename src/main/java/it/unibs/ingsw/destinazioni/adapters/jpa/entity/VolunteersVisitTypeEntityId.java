package it.unibs.ingsw.destinazioni.adapters.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.Objects;

@Getter
@Setter
@Embeddable
public class VolunteersVisitTypeEntityId implements java.io.Serializable {
    private static final long serialVersionUID = 581313901065918127L;
    @NotNull
    @Column(name = "visit_type_id", nullable = false)
    private Integer visitTypeId;

    @NotNull
    @Column(name = "volunteer_id", nullable = false)
    private Integer volunteerId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        VolunteersVisitTypeEntityId entity = (VolunteersVisitTypeEntityId) o;
        return Objects.equals(this.visitTypeId, entity.visitTypeId) &&
                Objects.equals(this.volunteerId, entity.volunteerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(visitTypeId, volunteerId);
    }

}