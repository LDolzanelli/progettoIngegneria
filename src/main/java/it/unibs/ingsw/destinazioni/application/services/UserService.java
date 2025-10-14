package it.unibs.ingsw.destinazioni.application.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import it.unibs.ingsw.destinazioni.application.exceptions.codes.UserErrorCode;
import it.unibs.ingsw.destinazioni.application.exceptions.specific.UserException;
import it.unibs.ingsw.destinazioni.application.port.in.login.ChangeCredentialsUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.login.LoginUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.user.GetUserInfoUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.user.RegisterUserUseCase;
import it.unibs.ingsw.destinazioni.application.port.out.UserRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.dto.LoginRequestDTO;
import it.unibs.ingsw.destinazioni.domain.dto.LoginResponseDTO;
import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.model.enums.Role;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements LoginUseCase, GetUserInfoUseCase, ChangeCredentialsUseCase, RegisterUserUseCase {

  private final UserRepositoryPort userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  /*@ also
    @ requires userRepository != null && passwordEncoder != null;
    @ ensures userRepository.findByNickname(user.getNickname()).isPresent();
    @ ensures !userRepository.findByNickname(user.getNickname()).get().getPassword().equals(\old(user.getPassword()));
    @ ensures userRepository.findByNickname(user.getNickname()).get().isFirstLogin() == !user.getRole().equals(Role.FINAL_USER);
    @*/
  public void registerNewUser(User user) {
    if (userRepository.findByNickname(user.getNickname()).isPresent()) {
      throw new UserException(UserErrorCode.USER_ALREADY_EXISTS,
          "Esiste già un utente con nickname: " + user.getNickname());
    }
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    // se è un finalUser che si sta registrando, non deve reimpostare le credenziali
    user.setFirstLogin(!user.getRole().equals(Role.FINAL_USER));
    userRepository.save(user);
  }


  @Override
  /*@ also
    @ ensures userRepository.findByNickname(nickname).get().isFirstLogin() == false;
    @ ensures !passwordEncoder.matches(oldPassword, userRepository.findByNickname(nickname).get().getPassword());
    @*/
  public void changePassword(String nickname, String oldPassword, String newPassword) {

    User user = userRepository.findByNickname(nickname).orElseThrow(
        () -> new UserException(UserErrorCode.USER_NOT_FOUND, "Utente con nickname \"" + nickname + "\" non trovato"));

    if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
      throw new UserException(UserErrorCode.INVALID_CREDENTIALS, "Password errata");
    }

    user.setPassword(passwordEncoder.encode(newPassword));
    user.setFirstLogin(false);
    userRepository.save(user);
  }


  @Override
  /*@ also
    @ ensures userRepository.findByNickname(newNickname).isPresent();
    @ ensures userRepository.findByNickname(newNickname).get().getNickname().equals(newNickname);
    @ ensures userRepository.findByNickname(newNickname).get().isFirstLogin() == false;
    @ ensures !oldNickname.equals(newNickname) ==> !userRepository.findByNickname(oldNickname).isPresent();
    @*/
  public void changeUsername(String oldNickname, String newNickname) {
    if (oldNickname.equals(newNickname) && userRepository.findByNickname(newNickname).isPresent()) {
      throw new UserException(UserErrorCode.USER_ALREADY_EXISTS,
          "Nickname \"" + newNickname + "\" già esistente. Riprovare.");
    }

    User user =
        userRepository.findByNickname(oldNickname).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND,
            "Utente con nickname \"" + oldNickname + "\" non trovato"));
    user.setNickname(newNickname);
    user.setFirstLogin(false);
    userRepository.save(user);
  }


  @Override
  /*@ also
    @ ensures userRepository.findByNickname(newNickname).isPresent();
    @ ensures userRepository.findByNickname(newNickname).get().isFirstLogin() == false;
    @*/
  public void changeBothCredentials(String oldNickname, String newNickname, String oldPassword, String newPassword) {
    User user =
        userRepository.findByNickname(oldNickname).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND,
            "Utente con nickname \"" + oldNickname + "\" non trovato"));

    //TODO: eccezione specifica per i volontari
    if (user.getRole() == Role.VOLUNTEER && !oldNickname.equals(newNickname)) {
      throw new UserException(UserErrorCode.UNAUTHORIZED_REQUEST,
          "I volontari non possono cambiare il proprio nickname");
    }

    if (!oldNickname.equals(newNickname)) {
      changeUsername(oldNickname, newNickname);
      changePassword(newNickname, oldPassword, newPassword);
    } else {
      changePassword(oldNickname, oldPassword, newPassword);
    }
  }


  @Override
  /*@ also
    @ ensures \result.isPresent() ==> \result.get().getId() == id;
    @*/
  public Optional<User> findById(int id) {
    return userRepository.findById(id);
  }


  @Override
  /*@ also
    @ ensures \result.isPresent() ==> \result.get().getNickname().equals(nickname);
    @*/
  public Optional<User> findByNickname(String nickname) {
    return userRepository.findByNickname(nickname);
  }


  @Override
  /*@ also
    @ ensures \result.nickname().equals(loginRequestDTO.nickname());
    @ ensures \result.role().equals(optionalUser.get().getRole().getName());
    @ ensures \result.firstLogin() == optionalUser.get().isFirstLogin();
    @*/
  public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
    Optional<User> optionalUser = userRepository.findByNickname(loginRequestDTO.nickname());

    if (optionalUser.isEmpty()) {
      throw new UserException(UserErrorCode.USER_NOT_FOUND,
          "Utente con nickname \"" + loginRequestDTO.nickname() + "\" non trovato");
    }

    User user = optionalUser.get();
    if (!passwordEncoder.matches(loginRequestDTO.password(), user.getPassword())) {
      throw new UserException(UserErrorCode.INVALID_CREDENTIALS, "Password errata");
    }

    return new LoginResponseDTO(user.getNickname(), user.getRole().getName(), user.isFirstLogin());
  }


  @Override
  /*@ also
    @ ensures (\forall User u; \result.contains(u); u.getRole() == role);
    @*/
  public List<User> getUsersByRole(Role role) {
    return userRepository.findAllByRole(role);
  }

  @Override
  /*@ also
    @ ensures (\forall User u; \result.contains(u); u.getRole() == role);
    @*/
  public List<Integer> getUsersIdsByRole(Role role) {
    return userRepository.findAllByRole(role).stream() //
            .map(User::getId) //
            .collect(Collectors.toList());
  }


  @Override
  /*@ also
    @ ensures \result.size() == nicknames.size();
    @ ensures (\forall User u; \result.contains(u); nicknames.contains(u.getNickname()));
    @*/
  public List<User> findAllByNicknames(List<String> nicknames) {

    return nicknames.stream() //
            .map(nick -> userRepository.findByNickname(nick) //
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND, "Utente con nickname \"" + nick + "\" non trovato"))) //
            .toList();
  }


  @Override
  /*@ also
    @ ensures \result > 0;
    @ ensures findById(\result).isPresent();
    @ ensures findById(\result).get().getNickname().equals(nickname);
    @*/
  public int getIdByNickname(String nickname) {
    Optional<User> user = findByNickname(nickname);
    return user.orElseThrow(
        () -> new UserException(UserErrorCode.USER_NOT_FOUND, "Utente con nickname \"" + nickname + "\" non trovato"))
        .getId();
  }
}
