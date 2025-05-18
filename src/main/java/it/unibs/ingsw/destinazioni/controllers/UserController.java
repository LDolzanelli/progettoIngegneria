package it.unibs.ingsw.destinazioni.controllers;

import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.dto.ChangePasswordDTO;
import it.unibs.ingsw.destinazioni.domain.dto.LoginRequestDTO;
import it.unibs.ingsw.destinazioni.domain.dto.LoginResponseDTO;
import it.unibs.ingsw.destinazioni.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        return userService.findByNickname(request.nickname())
                .filter(user -> passwordEncoder.matches(request.password(), user.getPassword()))
                .map(user -> new LoginResponseDTO(user.getNickname(), user.getRole(), user.isFirstLogin()))
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenziali errate"));
    }


    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordDTO dto) {
        User user = userService.findByNickname(dto.nickname())
                .filter(u -> passwordEncoder.matches(dto.oldPassword(), u.getPassword())).filter(User::isFirstLogin)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Richiesta non valida"));

        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        user.setFirstLogin(false);
        userService.updatePassword(user.getId(), user.getPassword());

        return ResponseEntity.ok().build();
    }


    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}
