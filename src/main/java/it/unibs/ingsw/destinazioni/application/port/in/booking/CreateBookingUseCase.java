package it.unibs.ingsw.destinazioni.application.port.in.booking;

import java.util.List;

import it.unibs.ingsw.destinazioni.application.exceptions.usecases.BookingException;
import it.unibs.ingsw.destinazioni.application.exceptions.usecases.UserException;
import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.model.Visit;

/**
 * Interface per la creazione di nuove prenotazioni delle visite.
 */
public interface CreateBookingUseCase {

    /**
     * Prenota una visita per un utente con una lista di visitatori.
     * 
     * @param visit         la visita da prenotare
     * @param user          l'utente che effettua la prenotazione
     * @param visitorsNames lista dei nomi dei visitatori
     * @throws UserException se l'utente non è di tipo FINAL_USER
     * @throws BookingException         se non ci sono posti disponibili
     */
    /*@ requires visit != null && user != null && visitorsNames != null;
      @ requires user.getRole() == Role.FINAL_USER;
      @ requires visitorsNames.size() > 0;
      @ requires visitorsNames.size() <= visit.getAvailableSeats();
      @ requires (\forall String name; visitorsNames.contains(name);
      @          name != null && !name.trim().isEmpty());
      @ ensures (\exists BookingQueryUseCase query;
      @          query.getBookingsByVisit(visit).stream().anyMatch(b ->
      @          b.getUser().equals(user) && b.getVisitorsNames().equals(visitorsNames)));
      @ signals (UserException e) user.getRole() != Role.FINAL_USER;
      @ signals (BookingException e) visitorsNames.size() > visit.getAvailableSeats();
      @*/
    public void bookVisit(Visit visit, User user, List<String> visitorsNames);
}
