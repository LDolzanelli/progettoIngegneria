package it.unibs.ingsw.destinazioni.application.port.in.login;

/**
 * Interface per la gestione del cambio credenziali.
 */
/*@ public invariant (\forall String nickname; nickname != null && !nickname.trim().isEmpty();
  @                   findByNickname(nickname).isPresent() ==>
  @                   findByNickname(nickname).get().getNickname().equals(nickname));
  @*/
public interface ChangeCredentialsUseCase {

  /**
   * Cambia la password di un utente.
   * 
   * @param nickname nickname dell'utente
   * @param oldPassword password corrente
   * @param newPassword nuova password
   * @throws IllegalArgumentException se l'utente non esiste o la password corrente è errata
   */
  /*@ requires nickname != null && !nickname.trim().isEmpty();
    @ requires oldPassword != null && !oldPassword.trim().isEmpty();
    @ requires newPassword != null && !newPassword.trim().isEmpty();
    @ requires findByNickname(nickname).isPresent();
    @ ensures findByNickname(nickname).get().isFirstLogin() == false;
    @ ensures !findByNickname(nickname).get().getPassword().equals(oldPassword);
    @ signals (IllegalArgumentException e) !findByNickname(nickname).isPresent() ||
    @                                      !passwordMatches(oldPassword, findByNickname(nickname).get().getPassword());
    @*/
  void changePassword(String nickname, String oldPassword, String newPassword);


  /**
   * Cambia il nickname di un utente.
   * 
   * @param oldNickname nickname corrente
   * @param newNickname nuovo nickname
   * @throws IllegalArgumentException se l'utente non esiste o il nuovo nickname è già in uso
   */
  /*@ requires oldNickname != null && !oldNickname.trim().isEmpty();
    @ requires newNickname != null && !newNickname.trim().isEmpty();
    @ requires findByNickname(oldNickname).isPresent();
    @ requires !oldNickname.equals(newNickname) ==> !findByNickname(newNickname).isPresent();
    @ ensures !findByNickname(oldNickname).isPresent() || oldNickname.equals(newNickname);
    @ ensures findByNickname(newNickname).isPresent();
    @ ensures findByNickname(newNickname).get().isFirstLogin() == false;
    @ signals (IllegalArgumentException e) !findByNickname(oldNickname).isPresent() ||
    @                                      (!oldNickname.equals(newNickname) && findByNickname(newNickname).isPresent());
    @*/
  void changeUsername(String oldNickname, String newNickname);


  /**
   * Cambia sia il nickname che la password di un utente.
   * 
   * @param oldNickname nickname corrente
   * @param newNickname nuovo nickname
   * @param oldPassword password corrente
   * @param newPassword nuova password
   * @throws IllegalArgumentException se l'utente non esiste, la password è errata, 
   *                                 il nuovo nickname è già in uso, o un volontario tenta di cambiare nickname
   */
  /*@ requires oldNickname != null && !oldNickname.trim().isEmpty();
    @ requires newNickname != null && !newNickname.trim().isEmpty();
    @ requires oldPassword != null && !oldPassword.trim().isEmpty();
    @ requires newPassword != null && !newPassword.trim().isEmpty();
    @ requires findByNickname(oldNickname).isPresent();
    @ requires findByNickname(oldNickname).get().getRole() != Role.VOLUNTEER || oldNickname.equals(newNickname);
    @ ensures findByNickname(newNickname).isPresent();
    @ ensures findByNickname(newNickname).get().isFirstLogin() == false;
    @ signals (IllegalArgumentException e) !findByNickname(oldNickname).isPresent() ||
    @                                      (findByNickname(oldNickname).get().getRole() == Role.VOLUNTEER && !oldNickname.equals(newNickname));
    @*/
  void changeBothCredentials(String oldNickname, String newNickname, String oldPassword, String newPassword);
}
