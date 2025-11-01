package it.unibs.ingsw.destinazioni.application.port.in.volunteer;

import it.unibs.ingsw.destinazioni.domain.model.User;

/**
 * Interface per la rimozione dei volontari dal sistema.
 */
public interface RemoveVolunteerUseCase {

  /**
   * Rimuove un volontario dal sistema.
   * Prima di rimuovere il volontario, lo rimuove da tutti i tipi di visita
   * a cui è assegnato. Se un tipo di visita rimane senza volontari,
   * viene rimosso anch'esso.
   * 
   * @param volunteer il volontario da rimuovere
   * @throws UserException se il volontario non può essere rimosso
   */
  /*@ requires volunteer != null;
    @ requires volunteer.getRole() == Role.VOLUNTEER;
    @ requires (\exists VolunteerValidationUseCase validation; 
    @           validation.canBeRemoved(volunteer));
    @ ensures !(\exists User u; userRepository.findAll().contains(u);
    @           u.getNickname().equals(volunteer.getNickname()));
    @ ensures (\forall VisitType vt; 
    @          !vt.getVolunteers().stream().anyMatch(v -> 
    @           v.getNickname().equals(volunteer.getNickname())));
    @ signals (UserException e) !(\exists VolunteerValidationUseCase validation; 
    @                                        validation.canBeRemoved(volunteer));
    @*/
  void removeVolunteer(User volunteer);
}
