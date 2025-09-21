package it.unibs.ingsw.destinazioni.application.port.in.booking;

import java.util.List;

import it.unibs.ingsw.destinazioni.application.exceptions.BookingException;
import it.unibs.ingsw.destinazioni.domain.model.Booking;
import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.model.Visit;

/**
 * Interface per le operazioni di query sulle prenotazioni delle visite.
 */
/*@ 
  @ public invariant (\forall Booking b; getBookingsByUser(b.getUser()).contains(b);
  @                  b.getUser() != null && b.getBookingCode() != null);
  @ public invariant (\forall Visit v; getBookingsByVisit(v) != null;
  @                  v.getId() != null);
  @*/
public interface BookingQueryUseCase {

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
