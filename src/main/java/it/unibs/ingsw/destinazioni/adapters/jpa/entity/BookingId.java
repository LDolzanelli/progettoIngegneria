package it.unibs.ingsw.destinazioni.adapters.jpa.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class BookingId implements Serializable {

    @NotNull
    private String bookingCode;

    @NotNull
    private String visitorName;

    public BookingId() {}

    public BookingId(String bookingCode, String visitorName) {
        this.bookingCode = bookingCode;
        this.visitorName = visitorName;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookingId)) return false;
        BookingId that = (BookingId) o;
        return Objects.equals(bookingCode, that.bookingCode) &&
               Objects.equals(visitorName, that.visitorName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingCode, visitorName);
    }
}
