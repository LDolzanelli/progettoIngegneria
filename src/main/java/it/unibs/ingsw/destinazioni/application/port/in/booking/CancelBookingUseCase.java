package it.unibs.ingsw.destinazioni.application.port.in.booking;

import it.unibs.ingsw.destinazioni.application.exceptions.usecases.BookingException;

/**
 * Interface per la cancellazione delle prenotazioni delle visite.
 */
public interface CancelBookingUseCase {

    /**
     * Cancella una prenotazione utilizzando il codice di prenotazione.
     * 
     * @param bookingCode codice della prenotazione da cancellare
     * @param userId      ID dell'utente che vuole cancellare la prenotazione
     * @throws BookingException se la prenotazione non esiste, l'utente non è
     *                          autorizzato,
     *                          o la prenotazione non è cancellabile
     */
    /*@ requires bookingCode != null && !bookingCode.trim().isEmpty();
      @ requires userId > 0;
      @ requires isThisBookingCancellable(bookingCode);
      @ requires (\exists BookingQueryUseCase query;
      @          query.getBookingByCode(bookingCode).getUser().getId() == userId);
      @ ensures !(\exists BookingQueryUseCase query;
      @          query.getBookingsByUser(query.getBookingByCode(bookingCode).getUser())
      @          .stream().anyMatch(b -> b.getBookingCode().equals(bookingCode)));
      @ signals (BookingException e) !isThisBookingCancellable(bookingCode) ||
      @         (\exists BookingQueryUseCase query;
      @          query.getBookingByCode(bookingCode).getUser().getId() != userId);
      @*/
    public void cancelBooking(String bookingCode, int userId);

    /**
     * Verifica se una prenotazione è cancellabile.
     * 
     * @param bookingCode codice della prenotazione da verificare
     * @return true se la prenotazione è cancellabile, false altrimenti
     * @throws BookingException se la prenotazione non esiste
     */
    /*@ requires bookingCode != null && !bookingCode.trim().isEmpty();
      @ ensures \result == (\exists BookingQueryUseCase query;
      @                     query.getVisitByBookingCode(bookingCode).getVisitStatus() ==
      @                     VisitStatus.PROPOSED ||
      @                     query.getVisitByBookingCode(bookingCode).getVisitStatus() ==
      @                     VisitStatus.FULL);
      @ signals (BookingException e) (\exists BookingQueryUseCase query;
      @                              query.getBookingByCode(bookingCode) == null);
      @ pure
      @*/
    public boolean isThisBookingCancellable(String bookingCode);
}
