package it.unibs.ingsw.destinazioni.ui.rest.controller;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import it.unibs.ingsw.destinazioni.application.port.in.login.ChangeCredentialsUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.login.LoginUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.user.GetUserInfoUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.user.RegisterUserUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.visittype.VisitTypeQueryUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.volunteer.RemoveVolunteerUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.volunteer.VolunteerValidationUseCase;
import it.unibs.ingsw.destinazioni.domain.dto.ChangeCredentialsDTO;
import it.unibs.ingsw.destinazioni.domain.dto.ChangePasswordDTO;
import it.unibs.ingsw.destinazioni.domain.dto.ChangeUserNameDTO;
import it.unibs.ingsw.destinazioni.domain.dto.LoginRequestDTO;
import it.unibs.ingsw.destinazioni.domain.dto.LoginResponseDTO;
import it.unibs.ingsw.destinazioni.domain.dto.RegisterUserDTO;
import it.unibs.ingsw.destinazioni.domain.dto.VolunteerDTO;
import it.unibs.ingsw.destinazioni.domain.dto.VolunteerWithVisitsDTO;
import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.model.VisitType;
import it.unibs.ingsw.destinazioni.domain.model.enums.Role;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final LoginUseCase loginService;
    private final ChangeCredentialsUseCase changeCredentialsService;
    private final GetUserInfoUseCase userInfoService;
    private final RegisterUserUseCase registerUserService;
    private final VisitTypeQueryUseCase visitTypeQueryService;
    private final VolunteerValidationUseCase volunteerValidationService;
    private final RemoveVolunteerUseCase removeVolunteerService;


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {

        LoginResponseDTO response = loginService.login(request);
        return ResponseEntity.ok(response);

    }


    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordDTO dto) {


        changeCredentialsService.changePassword(dto.username(), dto.oldPassword(), dto.newPassword());
        return ResponseEntity.ok().build();

    }


    @PostMapping("/change-username")
    public ResponseEntity<Void> changeUsername(@RequestBody ChangeUserNameDTO dto) {


        changeCredentialsService.changeUsername(dto.oldUserName(), dto.newUserName());
        return ResponseEntity.ok().build();

    }


    @PostMapping("/change-both-credentials")
    public ResponseEntity<String> changeBothCredentials(@RequestBody ChangeCredentialsDTO dto) {

        changeCredentialsService.changeBothCredentials(dto.oldUsername(), dto.newUsername(), dto.oldPassword(),
                dto.newPassword());
        return ResponseEntity.ok().build();

    }


    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterUserDTO dto) {

        User user = new User(dto.nickname(), dto.password(), Role.fromString(dto.role()));
        registerUserService.registerNewUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();

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
        Set<VisitType> allVisitTypes = visitTypeQueryService.listAll();

        // associa i due
        return volunteers.stream().map(volunteer -> {
            String nickname = volunteer.getNickname();
            List<String> visitTitles = allVisitTypes.stream()
                    .filter(visit -> visit.getVolunteers().stream()
                            .anyMatch(v -> v.getNickname().equals(volunteer.getNickname())))
                    .map(VisitType::getTitle).toList();
            return new VolunteerWithVisitsDTO(nickname, visitTitles,
                    volunteerValidationService.canBeRemoved(volunteer));
        }).toList();
    }


    @GetMapping("/get-id/{username}")
    public ResponseEntity<Integer> getId(@PathVariable String username) {
        int id = userInfoService.getIdByNickname(username);
        return ResponseEntity.ok(id);
    }


    @GetMapping("/get-role/{username}")
    public ResponseEntity<String> getRole(@PathVariable String username) {
        Optional<User> user = userInfoService.findByNickname(username);
        String role = user.get().getRole().toString();
        return ResponseEntity.ok(role);
    }


    @DeleteMapping("/remove-volunteer/{nickname}")
    public ResponseEntity<String> removeVolunteer(@PathVariable String nickname) {

        User volunteer = userInfoService.findByNickname(nickname).get();

        removeVolunteerService.removeVolunteer(volunteer);
        return ResponseEntity.ok("Volontario rimosso con successo");

    }

}
