package it.unibs.ingsw.destinazioni.application.port.in.visit;

import java.util.List;

import it.unibs.ingsw.destinazioni.application.exceptions.usecases.UserException;
import it.unibs.ingsw.destinazioni.application.exceptions.usecases.VisitDayException;
import it.unibs.ingsw.destinazioni.domain.model.Visit;

/**
 * Interface per la gestione dei giorni di visita.
 */
/*@ 
  @ public invariant (\forall Visit v; getVisitById(v.getId()) != null;
  @                  v.getId() != null && v.getId() > 0);
  @ public invariant (\forall String nickname; getConfirmedVisitsPerVolunteer(nickname) != null;
  @                  nickname != null && !nickname.trim().isEmpty());
  @*/
public interface VisitDaysUseCase {

    /**
     * Crea le visite di default per un mese specificato. Le visite vengono
     * create con status "PROPOSED".
     *
     * @param month il mese per cui creare le visite (deve essere il mese
     * corrente + 2)
     * @throws VisitDayException se il mese non è valido o se non è
     * possibile creare visite per il mese specificato
     */
    /*@ requires month >= 1 && month <= 12;
    @ requires month == (\current_month + 2) % 12 || (month == (\current_month + 2) && month <= 12);
    @ requires \current_day >= 15;
    @ ensures (\forall VisitType vt; 
    @          (\exists Visit v; v.getDate().getMonthValue() == month && 
    @           v.getVisitType().equals(vt) && v.getVisitStatus() == VisitStatus.PROPOSED));
    @ signals (VisitDayException e) month < 1 || month > 12 ||
    @                                       month != (\current_month + 2) % 12 ||
    @                                       \current_day < 15;
    @*/
    void createDefaultVisitDays(int month);



    /**
     * Ottiene tutte le visite confermate assegnate a un volontario specifico.
     *
     * @param volunteerNickname il nickname del volontario
     * @return lista delle visite confermate per il volontario
     * @throws UserException se il volontario non esiste o non ha ruolo VOLUNTEER
     * ruolo VOLUNTEER
     */
    /*@ requires volunteerNickname != null && !volunteerNickname.trim().isEmpty();
    @ ensures \result != null;
    @ ensures (\forall Visit v; \result.contains(v); 
    @          v.getVolunteer().getNickname().equals(volunteerNickname) &&
    @          v.getVisitStatus() == VisitStatus.CONFIRMED);
    @ signals (UserException e) volunteerNickname == null ||
    @                                       volunteerNickname.trim().isEmpty() ||
    @                                       !(\exists User u; u.getNickname().equals(volunteerNickname) &&
    @                                         u.getRole() == Role.VOLUNTEER);
    @ pure
    @*/
    List<Visit> getConfirmedVisitsPerVolunteer(String volunteerNickname);

    /**
     * Ottiene una visita tramite il suo ID.
     *
     * @param visitId l'ID della visita
     * @return la visita corrispondente all'ID
     * @throws VisitDayException se la visita non esiste
     */
    /*@ requires visitId > 0;
    @ ensures \result != null;
    @ ensures \result.getId() == visitId;
    @ signals (VisitDayException e) visitId <= 0 || 
    @                                       !(\exists Visit v; v.getId() == visitId);
    @ pure
    @*/
    Visit getVisitById(int visitId);
}
