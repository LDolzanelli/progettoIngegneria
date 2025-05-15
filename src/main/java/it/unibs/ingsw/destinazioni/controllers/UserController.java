package it.unibs.ingsw.destinazioni.controllers;


import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.services.UserService;
import java.util.Optional;

@PermitAll
@RestController
@RequestMapping(value = "/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping("{nickname}")
    public Optional<User> findUser(@PathVariable("nickname") String nickname) {
        return service.findByNickname(nickname);
    }

    @PostMapping("/{userId}/updateNickname")
    public User updateNickname(@PathVariable("userId") int userId, @RequestBody String newNickname) {
        return service.updateNickname(userId, newNickname);
    }

    @PostMapping("/{userId}/updatePassword")
    public User updatePassword(@PathVariable("userId") int userId, @RequestBody String newPassword) {
        return service.updatePassword(userId, newPassword);
    }

}
