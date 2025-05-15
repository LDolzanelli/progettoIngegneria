package it.unibs.ingsw.destinazioni.adapters.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.Objects;

@Getter
@Setter
@Embeddable
public class VisitDayIdEntity implements java.io.Serializable {
    private static final long serialVersionUID = -3284533581211054117L;
    @NotNull
    @Column(name = "visit_type_id", nullable = false)
    private Integer visitTypeId;

    @Size(max = 9)
    @NotNull
    @Column(name = "day_of_week", nullable = false, length = 9)
    private String dayOfWeek;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        VisitDayIdEntity entity = (VisitDayIdEntity) o;
        return Objects.equals(this.dayOfWeek, entity.dayOfWeek) &&
                Objects.equals(this.visitTypeId, entity.visitTypeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayOfWeek, visitTypeId);
    }

}