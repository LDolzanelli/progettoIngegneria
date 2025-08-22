package it.unibs.ingsw.destinazioni.application.port.in;

import java.util.List;

import it.unibs.ingsw.destinazioni.domain.model.Visit;
import it.unibs.ingsw.destinazioni.domain.model.Booking;
import it.unibs.ingsw.destinazioni.domain.model.User;

public interface BookingVisitsUseCase {

    public void bookVisit(Visit visit, User user,
            List<String> visitorsNames);

    public List<Booking> getBookingsByVisit(Visit visit);
    public List<Booking> getBookingsByUser(User user);

    public void cancelBooking(String bookingCode);

    public boolean isThisBookingCancellable(String bookingCode);
    
    
}
