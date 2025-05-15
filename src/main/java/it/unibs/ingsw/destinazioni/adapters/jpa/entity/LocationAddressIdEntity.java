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
public class LocationAddressIdEntity implements java.io.Serializable {
    private static final long serialVersionUID = 963444185785180780L;
    @Size(max = 100)
    @NotNull
    @Column(name = "street", nullable = false, length = 100)
    private String street;

    @Size(max = 20)
    @NotNull
    @Column(name = "number", nullable = false, length = 20)
    private String number;

    @Size(max = 100)
    @NotNull
    @Column(name = "town", nullable = false, length = 100)
    private String town;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        LocationAddressIdEntity entity = (LocationAddressIdEntity) o;
        return Objects.equals(this.number, entity.number) &&
                Objects.equals(this.town, entity.town) &&
                Objects.equals(this.street, entity.street);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, town, street);
    }

}