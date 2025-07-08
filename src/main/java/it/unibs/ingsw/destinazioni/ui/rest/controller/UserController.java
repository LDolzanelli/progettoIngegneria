package it.unibs.ingsw.destinazioni.ui.rest.controller;

import it.unibs.ingsw.destinazioni.application.port.in.*;
import it.unibs.ingsw.destinazioni.domain.dto.*;
import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.model.VisitType;
import it.unibs.ingsw.destinazioni.domain.model.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final LoginUseCase loginService;
    private final ChangeCredentialsUseCase changeCredentialsService;
    private final GetUserInfoUseCase userInfoService;
    private final RegisterUserUseCase registerUserService;
    private final ManageVisitTypeUseCase manageVisitTypeUseCase;


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        try {
            LoginResponseDTO response = loginService.login(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenziali errate");
        }
    }


    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordDTO dto) {

        try {
            changeCredentialsService.changePassword(dto.username(), dto.oldPassword(), dto.newPassword());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Richiesta non valida: " + e.getMessage());
        }
    }


    @PostMapping("/change-username")
    public ResponseEntity<Void> changeUsername(@RequestBody ChangeUserNameDTO dto) {

        try {
            changeCredentialsService.changeUsername(dto.oldUserName(), dto.newUserName());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Richiesta non valida: " + e.getMessage());
        }
    }


    @PostMapping("/change-both-credentials")
    public ResponseEntity<String> changeBothCredentials(@RequestBody ChangeCredentialsDTO dto) {
        try {
            changeCredentialsService.changeBothCredentials(dto.oldUsername(), dto.newUsername(), dto.oldPassword(),
                    dto.newPassword());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterUserDTO dto) {
        try {
            User user = new User(dto.nickname(), dto.password(), Role.fromString(dto.role()));
            registerUserService.registerNewUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @GetMapping("/info/{username}")
    public ResponseEntity<LoginResponseDTO> getUserInfo(@PathVariable String username) {
        return userInfoService.findByNickname(username)
                .map(user -> new LoginResponseDTO(user.getNickname(), user.getRole().getName(), user.isFirstLogin()))
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utente non trovato"));
    }


    @GetMapping("/list_volunteers")
    public List<VolunteerDTO> listVolunteers() {
        return userInfoService.getUsersByRole(Role.VOLUNTEER).stream().map(user -> new VolunteerDTO(user.getNickname()))
                .toList();
    }


    @GetMapping("/volunteers-with-visits")
    public List<VolunteerWithVisitsDTO> getVolunteersWithVisits() {
        // carica tutti i volontari
        List<User> volunteers = userInfoService.getUsersByRole(Role.VOLUNTEER);

        // carica tutte le visitType
        Set<VisitType> allVisitTypes = manageVisitTypeUseCase.listAll();

        // associa i due
        return volunteers.stream().map(volunteer -> {
            String nickname = volunteer.getNickname();
            List<String> visitTitles = allVisitTypes.stream()
                    .filter(visit -> visit.getVolunteers().stream()
                            .anyMatch(v -> v.getNickname().equals(volunteer.getNickname()))) // <-- confronto sicuro
                    .map(VisitType::getTitle).toList();
            return new VolunteerWithVisitsDTO(nickname, visitTitles);
        }).toList();
    }

}
