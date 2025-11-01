package it.unibs.ingsw.destinazioni.application.port.in.user;

import java.util.List;

import it.unibs.ingsw.destinazioni.application.exceptions.usecases.UserException;
import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.model.enums.Role;

/**
 * Interface per ottenere informazioni sugli utenti del sistema.
 */
/*@ public invariant (\forall User u; findById(u.getId()).isPresent();
  @                   findByNickname(u.getNickname()).isPresent());
  @*/
public interface GetUserInfoUseCase {

  /**
   * Trova un utente tramite il suo ID.
   * 
   * @param id l'ID dell'utente
   * @return l'utente trovato
   * @throws UserException se l'utente non esiste
   */
  /*@ requires id > 0;
    @ ensures \result != null;
    @ ensures \result.getId() == id;
    @ signals (UserException e) !userRepository.findById(id).isPresent();
    @ pure
    @*/
  User findById(int id);


  /**
   * Trova un utente tramite il suo nickname.
   * 
   * @param nickname il nickname dell'utente
   * @return l'utente trovato
   * @throws UserException se l'utente non esiste
   */
  /*@ requires nickname != null && !nickname.trim().isEmpty();
    @ ensures \result != null;
    @ ensures \result.getNickname().equals(nickname);
    @ signals (UserException e) !userRepository.findByNickname(nickname).isPresent();
    @ pure
    @*/
  User findByNickname(String nickname);


  /**
   * Ottiene l'ID di un utente tramite il suo nickname.
   * 
   * @param nickname il nickname dell'utente
   * @return l'ID dell'utente
   * @throws UserException se l'utente non esiste
   */
  /*@ requires nickname != null && !nickname.trim().isEmpty();
    @ requires findByNickname(nickname).isPresent();
    @ ensures \result > 0;
    @ ensures findById(\result).isPresent();
    @ ensures findById(\result).get().getNickname().equals(nickname);
    @ signals (UserException e) !findByNickname(nickname).isPresent();
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
   * @throws UserException se uno dei nickname non esiste
   */
  /*@ requires nicknames != null;
    @ requires (\forall String nick; nicknames.contains(nick); 
    @          nick != null && !nick.trim().isEmpty());
    @ requires (\forall String nick; nicknames.contains(nick); 
    @          findByNickname(nick).isPresent());
    @ ensures \result != null;
    @ ensures \result.size() == nicknames.size();
    @ ensures (\forall User u; \result.contains(u); nicknames.contains(u.getNickname()));
    @ signals (UserException e) 
    @         (\exists String nick; nicknames.contains(nick); !findByNickname(nick).isPresent());
    @ pure
    @*/
  List<User> findAllByNicknames(List<String> nicknames);

  List<Integer> getUsersIdsByRole(Role role);
}
