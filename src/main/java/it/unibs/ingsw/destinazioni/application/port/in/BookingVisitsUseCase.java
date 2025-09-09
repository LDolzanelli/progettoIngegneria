package it.unibs.ingsw.destinazioni.application.port.in;

import java.util.List;

import it.unibs.ingsw.destinazioni.application.exceptions.BookingException;
import it.unibs.ingsw.destinazioni.domain.model.Booking;
import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.model.Visit;

/**
 * Interface per la gestione delle prenotazioni delle visite.
 */
/*@ 
  @ public invariant (\forall Booking b; getBookingsByUser(b.getUser()).contains(b);
  @                  b.getUser() != null && b.getBookingCode() != null);
  @ public invariant (\forall Visit v; getBookingsByVisit(v) != null;
  @                  v.getId() != null);
  @*/
public interface BookingVisitsUseCase {

  /**
   * Prenota una visita per un utente con una lista di visitatori.
   *  
   * @param visit la visita da prenotare
   * @param user l'utente che effettua la prenotazione
   * @param visitorsNames lista dei nomi dei visitatori
   * @throws IllegalArgumentException se l'utente non è di tipo FINAL_USER
   * @throws BookingException se non ci sono posti disponibili
   */
  /*@ requires visit != null && user != null && visitorsNames != null;
    @ requires user.getRole() == Role.FINAL_USER;
    @ requires visitorsNames.size() > 0;
    @ requires visitorsNames.size() <= visit.getAvailableSeats();
    @ requires (\forall String name; visitorsNames.contains(name); name != null && !name.trim().isEmpty());
    @ ensures (\exists Booking b; getBookingsByVisit(visit).contains(b);
    @          b.getUser().equals(user) && b.getVisitorsNames().equals(visitorsNames));
    @ signals (IllegalArgumentException e) user.getRole() != Role.FINAL_USER;
    @ signals (BookingException e) visitorsNames.size() > visit.getAvailableSeats();
    @*/
  public void bookVisit(Visit visit, User user, List<String> visitorsNames);


  /**
   * Ottiene tutte le prenotazioni per una specifica visita.
   * 
   * @param visit la visita di cui ottenere le prenotazioni
   * @return lista delle prenotazioni per la visita
   * @throws IllegalArgumentException se la visita non esiste
   */
  /*@ requires visit != null && visit.getId() != null;
    @ ensures \result != null;
    @ ensures (\forall Booking b; \result.contains(b); 
    @          getBookingsByUser(b.getUser()).contains(b));
    @ signals (IllegalArgumentException e) visit.getId() == null;
    @ pure
    @*/
  public List<Booking> getBookingsByVisit(Visit visit);


  /**
   * Ottiene tutte le prenotazioni effettuate da un utente.
   * 
   * @param user l'utente di cui ottenere le prenotazioni
   * @return lista delle prenotazioni dell'utente
   * @throws IllegalArgumentException se l'utente non esiste
   */
  /*@ requires user != null && user.getId() != null;
    @ ensures \result != null;
    @ ensures (\forall Booking b; \result.contains(b); b.getUser().getId().equals(user.getId()));
    @ signals (IllegalArgumentException e) user.getId() == null;
    @ pure
    @*/
  public List<Booking> getBookingsByUser(User user);


  /**
   * Cancella una prenotazione utilizzando il codice di prenotazione.
   * 
   * @param bookingCode codice della prenotazione da cancellare
   * @param userId ID dell'utente che vuole cancellare la prenotazione
   * @throws BookingException se la prenotazione non esiste, l'utente non è autorizzato, 
   *                         o la prenotazione non è cancellabile
   */
  /*@ requires bookingCode != null && !bookingCode.trim().isEmpty();
    @ requires userId > 0;
    @ requires isThisBookingCancellable(bookingCode);
    @ requires getBookingByCode(bookingCode).getUser().getId() == userId;
    @ ensures !(\exists Booking b; getBookingsByUser(getBookingByCode(bookingCode).getUser()).contains(b);
    @           b.getBookingCode().equals(bookingCode));
    @ signals (BookingException e) !isThisBookingCancellable(bookingCode) ||
    @                              getBookingByCode(bookingCode).getUser().getId() != userId;
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
    @ ensures \result == (getVisitByBookingCode(bookingCode).getVisitStatus() == VisitStatus.PROPOSED ||
    @                    getVisitByBookingCode(bookingCode).getVisitStatus() == VisitStatus.FULL);
    @ signals (BookingException e) getBookingByCode(bookingCode) == null;
    @ pure
    @*/
  public boolean isThisBookingCancellable(String bookingCode);


  /**
   * Ottiene una prenotazione tramite il suo codice.
   * 
   * @param bookingCode codice della prenotazione
   * @return la prenotazione corrispondente al codice
   * @throws BookingException se la prenotazione non esiste
   */
  /*@ requires bookingCode != null && !bookingCode.trim().isEmpty();
    @ ensures \result != null;
    @ ensures \result.getBookingCode().equals(bookingCode);
    @ signals (BookingException e) bookingCode == null || bookingCode.trim().isEmpty();
    @ pure
    @*/
  public Booking getBookingByCode(String bookingCode);


  /**
   * Ottiene la visita associata a una prenotazione tramite il codice di prenotazione.
   * 
   * @param bookingCode codice della prenotazione
   * @return la visita associata alla prenotazione
   * @throws IllegalArgumentException se la visita non viene trovata
   */
  /*@ requires bookingCode != null && !bookingCode.trim().isEmpty();
    @ ensures \result != null;
    @ ensures getBookingsByVisit(\result).stream().anyMatch(b -> b.getBookingCode().equals(bookingCode));
    @ signals (IllegalArgumentException e) bookingCode == null || bookingCode.trim().isEmpty();
    @ pure
    @*/
  public Visit getVisitByBookingCode(String bookingCode);

}
