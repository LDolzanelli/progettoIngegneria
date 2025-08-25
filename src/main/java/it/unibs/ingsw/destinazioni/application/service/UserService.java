package it.unibs.ingsw.destinazioni.application.service;

import it.unibs.ingsw.destinazioni.application.port.in.ChangeCredentialsUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.GetUserInfoUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.LoginUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.RegisterUserUseCase;
import it.unibs.ingsw.destinazioni.application.port.out.UserRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.dto.LoginRequestDTO;
import it.unibs.ingsw.destinazioni.domain.dto.LoginResponseDTO;
import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.model.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements LoginUseCase, GetUserInfoUseCase, ChangeCredentialsUseCase, RegisterUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void registerNewUser(User user) {
        if (userRepository.findByNickname(user.getNickname()).isPresent()) {
            throw new IllegalArgumentException("Nickname già in uso");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        //se è un finalUser che si sta registrando, non deve reimpostare le credenziali
        user.setFirstLogin(!user.getRole().equals(Role.FINAL_USER));
        userRepository.save(user);
    }


    @Override
    public void changePassword(String nickname, String oldPassword, String newPassword) {

        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Password corrente errata");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setFirstLogin(false);
        userRepository.save(user);
    }


    @Override
    public void changeUsername(String oldNickname, String newNickname) {
        if (oldNickname.equals(newNickname) && userRepository.findByNickname(newNickname).isPresent()) {
            throw new IllegalArgumentException("Nickname \"" + newNickname + "\" già esistente. Riprovare.");
        }

        User user = userRepository.findByNickname(oldNickname)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));
        user.setNickname(newNickname);
        user.setFirstLogin(false);
        userRepository.save(user);
    }


    @Override
    public void changeBothCredentials(String oldNickname, String newNickname, String oldPassword, String newPassword) {
        User user = userRepository.findByNickname(oldNickname)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        if (user.getRole() == Role.VOLUNTEER && !oldNickname.equals(newNickname)) {
            throw new IllegalArgumentException("I volontari non possono modificare il nome utente");
        }

        if (!oldNickname.equals(newNickname)) {
            changeUsername(oldNickname, newNickname);
            changePassword(newNickname, oldPassword, newPassword);
        } else {
            changePassword(oldNickname, oldPassword, newPassword);
        }
    }


    @Override
    public Optional<User> findById(int id) {
        return userRepository.findById(id);
    }


    @Override
    public Optional<User> findByNickname(String nickname) {
        return userRepository.findByNickname(nickname);
    }


    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        Optional<User> optionalUser = userRepository.findByNickname(loginRequestDTO.nickname());

        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("Utente non trovato");
        }

        User user = optionalUser.get();
        if (!passwordEncoder.matches(loginRequestDTO.password(), user.getPassword())) {
            throw new IllegalArgumentException("Password errata");
        }

        return new LoginResponseDTO(user.getNickname(), user.getRole().getName(), user.isFirstLogin());
    }


    @Override
    public List<User> getUsersByRole(Role role) {
        return userRepository.findAllByRole(role);
    }


    @Override
    public List<User> findAllByNicknames(List<String> nicknames) {
        return nicknames.stream().map(nick -> userRepository.findByNickname(nick)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato: " + nick))).toList();
    }


    @Override
    public int getIdByNickname(String nickname) {
        Optional<User> user = findByNickname(nickname);
        return user.orElseThrow(() -> new IllegalArgumentException("Utente non trovato")).getId();
    }
}
