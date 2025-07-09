package it.unibs.ingsw.destinazioni.adapters.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class VolunteerAvailableDateEntityId implements java.io.Serializable {
    private static final long serialVersionUID = 2452783117730602210L;
    @NotNull
    @Column(name = "volunteer_id", nullable = false)
    private Integer volunteerId;

    @NotNull
    @Column(name = "available_date", nullable = false)
    private LocalDate availableDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        VolunteerAvailableDateEntityId entity = (VolunteerAvailableDateEntityId) o;
        return Objects.equals(this.availableDate, entity.availableDate) &&
                Objects.equals(this.volunteerId, entity.volunteerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(availableDate, volunteerId);
    }

}