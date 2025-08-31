package it.unibs.ingsw.destinazioni.application.port.in;

import java.util.List;
import java.util.Optional;

import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.model.enums.Role;

/*
 * @
 * 
 * @ public invariant (\forall User u; findById(u.getId()).isPresent();
 * 
 * @ findByNickname(u.getNickname()).isPresent());
 * 
 * @
 */
public interface GetUserInfoUseCase {

    /**
     * Trova un utente tramite il suo ID.
     * 
     * @param id l'ID dell'utente
     * @return Optional contenente l'utente se trovato
     */
    /*@ requires id > 0;
      @ ensures \result != null;
      @ ensures \result.isPresent() ==> \result.get().getId() == id;
      @ pure
      @*/
    Optional<User> findById(int id);


    /**
     * Trova un utente tramite il suo nickname.
     * 
     * @param nickname il nickname dell'utente
     * @return Optional contenente l'utente se trovato
     */
    /*@ requires nickname != null && !nickname.trim().isEmpty();
      @ ensures \result != null;
      @ ensures \result.isPresent() ==> \result.get().getNickname().equals(nickname);
      @ pure
      @*/
    Optional<User> findByNickname(String nickname);


    /**
     * Ottiene l'ID di un utente tramite il suo nickname.
     * 
     * @param nickname il nickname dell'utente
     * @return l'ID dell'utente
     * @throws IllegalArgumentException se l'utente non esiste
     */
    /*@ requires nickname != null && !nickname.trim().isEmpty();
      @ requires findByNickname(nickname).isPresent();
      @ ensures \result > 0;
      @ ensures findById(\result).isPresent();
      @ ensures findById(\result).get().getNickname().equals(nickname);
      @ signals (IllegalArgumentException e) !findByNickname(nickname).isPresent();
      @ pure
      @*/
    int getIdByNickname(String nickname);


    /**
     * Ottiene tutti gli utenti con un determinato ruolo.
     * 
     * @param role il ruolo degli utenti da cercare
     * @return lista degli utenti con il ruolo specificato
     */
    /*@ requires role != null;
      @ ensures \result != null;
      @ ensures (\forall User u; \result.contains(u); u.getRole() == role);
      @ pure
      @*/
    List<User> getUsersByRole(Role role);


    /**
     * Trova tutti gli utenti con i nickname specificati.
     * 
     * @param nicknames lista dei nickname da cercare
     * @return lista degli utenti trovati
     * @throws IllegalArgumentException se uno dei nickname non esiste
     */
    /*@ requires nicknames != null;
      @ requires (\forall String nick; nicknames.contains(nick); 
      @          nick != null && !nick.trim().isEmpty());
      @ requires (\forall String nick; nicknames.contains(nick); 
      @          findByNickname(nick).isPresent());
      @ ensures \result != null;
      @ ensures \result.size() == nicknames.size();
      @ ensures (\forall User u; \result.contains(u); nicknames.contains(u.getNickname()));
      @ signals (IllegalArgumentException e) 
      @         (\exists String nick; nicknames.contains(nick); !findByNickname(nick).isPresent());
      @ pure
      @*/
    List<User> findAllByNicknames(List<String> nicknames);
}
