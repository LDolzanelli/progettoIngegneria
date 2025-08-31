package it.unibs.ingsw.destinazioni.application.port.in;

import it.unibs.ingsw.destinazioni.domain.model.User;

/*
 * @
 * 
 * @ public invariant (\forall User u; u != null;
 * 
 * @ u.getNickname() != null && !u.getNickname().trim().isEmpty());
 * 
 * @
 */
public interface RegisterUserUseCase {

    /**
     * Registra un nuovo utente nel sistema.
     * 
     * @param user l'utente da registrare
     * @throws IllegalArgumentException se il nickname è già in uso o se l'utente è null
     */
    /*@ requires user != null;
      @ requires user.getNickname() != null && !user.getNickname().trim().isEmpty();
      @ requires user.getPassword() != null && !user.getPassword().trim().isEmpty();
      @ requires user.getRole() != null;
      @ ensures (\exists User savedUser; findByNickname(user.getNickname()).isPresent();
      @          savedUser.getNickname().equals(user.getNickname()));
      @ signals (IllegalArgumentException e) user == null || 
      @                                      user.getNickname() == null ||
      @                                      findByNickname(user.getNickname()).isPresent();
      @*/
    void registerNewUser(User user);
}
