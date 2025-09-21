package it.unibs.ingsw.destinazioni.application.port.in.volunteer;

import it.unibs.ingsw.destinazioni.domain.model.User;

/**
 * Interface per la validazione delle operazioni sui volontari.
 */
/*@ 
  @ public invariant (\forall User v; v.getRole() == Role.VOLUNTEER;
  @                  canBeRemoved(v) || !canBeRemoved(v));
  @*/
public interface VolunteerValidationUseCase {

  /**
   * Verifica se un volontario può essere rimosso dal sistema.
   * Un volontario può essere rimosso solo se non è assegnato a tipi di visita
   * che non possono essere rimossi dal sistema.
   * 
   * @param volunteer il volontario da verificare
   * @return true se il volontario può essere rimosso, false altrimenti
   * @throws IllegalArgumentException se l'utente non è un volontario
   */
  /*@ requires volunteer != null;
    @ requires volunteer.getRole() == Role.VOLUNTEER;
    @ ensures \result == (\forall VisitType vt; 
    @                    vt.getVolunteers().contains(volunteer);
    @                    (\exists VisitTypeValidationUseCase validation; validation.canBeRemoved(vt.getId())) || 
    @                    !vt.getVolunteers().contains(volunteer));
    @ signals (IllegalArgumentException e) volunteer.getRole() != Role.VOLUNTEER;
    @ pure
    @*/
  boolean canBeRemoved(User volunteer);
}
