package it.unibs.ingsw.destinazioni.application.port.in.login;

import it.unibs.ingsw.destinazioni.domain.dto.LoginRequestDTO;
import it.unibs.ingsw.destinazioni.domain.dto.LoginResponseDTO;

/**
 * Interface per la gestione del login degli utenti.
 */
/*@ public invariant (\forall LoginRequestDTO request; request != null;
  @                   request.nickname() != null && request.password() != null);
  @*/
public interface LoginUseCase {

  /**
   * Esegue il login di un utente.
   * 
   * @param loginRequestDTO DTO contenente nickname e password
   * @return DTO con informazioni dell'utente loggato
   * @throws IllegalArgumentException se l'utente non esiste o la password è errata
   */
  /*@ requires loginRequestDTO != null;
    @ requires loginRequestDTO.nickname() != null && !loginRequestDTO.nickname().trim().isEmpty();
    @ requires loginRequestDTO.password() != null && !loginRequestDTO.password().trim().isEmpty();
    @ requires findByNickname(loginRequestDTO.nickname()).isPresent();
    @ ensures \result != null;
    @ ensures \result.nickname().equals(loginRequestDTO.nickname());
    @ ensures findByNickname(loginRequestDTO.nickname()).get().getRole().getName().equals(\result.role());
    @ ensures \result.firstLogin() == findByNickname(loginRequestDTO.nickname()).get().isFirstLogin();
    @ signals (IllegalArgumentException e) !findByNickname(loginRequestDTO.nickname()).isPresent() ||
    @                                      !passwordMatches(loginRequestDTO.password(), findByNickname(loginRequestDTO.nickname()).get().getPassword());
    @ pure
    @*/
  LoginResponseDTO login(LoginRequestDTO loginRequestDTO);

}
