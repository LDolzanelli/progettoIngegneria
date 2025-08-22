package it.unibs.ingsw.destinazioni.domain.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Booking {
    private String bookingCode;
    private User user;
    private List<String> visitorsNames;

    public Booking(String bookingCode, User user, List<String> visitorsNames) {
        this.bookingCode = bookingCode;
        this.user = user;
        this.visitorsNames = visitorsNames;
    }

    public int getNumberOfVisitors() {
        return visitorsNames.size();
    }

}
