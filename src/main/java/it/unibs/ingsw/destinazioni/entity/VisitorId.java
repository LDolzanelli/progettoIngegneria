package it.unibs.ingsw.destinazioni.entity;

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
public class VisitorId implements java.io.Serializable {
    private static final long serialVersionUID = -3185293558365843399L;
    @NotNull
    @Column(name = "visit_id", nullable = false)
    private Integer visitId;

    @Size(max = 50)
    @NotNull
    @Column(name = "visitor_nickname", nullable = false, length = 50)
    private String visitorNickname;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        VisitorId entity = (VisitorId) o;
        return Objects.equals(this.visitId, entity.visitId) &&
                Objects.equals(this.visitorNickname, entity.visitorNickname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(visitId, visitorNickname);
    }

}