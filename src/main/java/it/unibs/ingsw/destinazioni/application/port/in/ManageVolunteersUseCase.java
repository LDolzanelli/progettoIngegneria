package it.unibs.ingsw.destinazioni.application.port.in;

import it.unibs.ingsw.destinazioni.domain.model.User;

/**
 * Interface per la gestione dei volontari.
 */
/*@ 
  @ public invariant (\forall User v; v.getRole() == Role.VOLUNTEER;
  @                  canBeRemoved(v) || !canBeRemoved(v));
  @*/
public interface ManageVolunteersUseCase {

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
      @                    canVisitTypeBeRemoved(vt.getId()) || 
      @                    !vt.getVolunteers().contains(volunteer));
      @ signals (IllegalArgumentException e) volunteer.getRole() != Role.VOLUNTEER;
      @ pure
      @*/
    boolean canBeRemoved(User volunteer);


    /**
     * Rimuove un volontario dal sistema.
     * Prima di rimuovere il volontario, lo rimuove da tutti i tipi di visita
     * a cui è assegnato. Se un tipo di visita rimane senza volontari,
     * viene rimosso anch'esso.
     * 
     * @param volunteer il volontario da rimuovere
     * @throws IllegalArgumentException se il volontario non può essere rimosso
     */
    /*@ requires volunteer != null;
      @ requires volunteer.getRole() == Role.VOLUNTEER;
      @ requires canBeRemoved(volunteer);
      @ ensures !(\exists User u; userRepository.findAll().contains(u);
      @           u.getNickname().equals(volunteer.getNickname()));
      @ ensures (\forall VisitType vt; 
      @          !vt.getVolunteers().stream().anyMatch(v -> 
      @           v.getNickname().equals(volunteer.getNickname())));
      @ signals (IllegalArgumentException e) !canBeRemoved(volunteer);
      @*/
    void removeVolunteer(User volunteer);
}
