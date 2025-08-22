package it.unibs.ingsw.destinazioni.application.port.out;

import java.util.List;
import java.util.Optional;

import it.unibs.ingsw.destinazioni.domain.model.Booking;

public interface BookingRepositoryPort {

    void save(Booking booking, int visitId);
    void deleteByBookingCode(String bookingCode);
    Optional<Booking> findByBookingCode(String bookingCode);
    List<Booking> findAll();
    List<Booking> findAllByVisitId(int visitId);
    List<Booking> findAllByUserId(int userId);
    
}
