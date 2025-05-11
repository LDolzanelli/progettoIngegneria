package it.unibs.ingsw.destinazioni.controller;

import it.unibs.ingsw.destinazioni.entity.UserEntity;
import it.unibs.ingsw.destinazioni.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(value = "/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping("{nickname}")
    public Optional<UserEntity> findUser(@PathVariable("nickname") String nickname) {
        return service.findByNickname(nickname);
    }

    @PostMapping("/{userId}/updateNickname")
    public UserEntity updateNickname(@PathVariable("userId") int userId, @RequestBody String newNickname) {
        return service.updateNickname(userId, newNickname);
    }

    @PostMapping("/{userId}/updatePassword")
    public UserEntity updatePassword(@PathVariable("userId") int userId, @RequestBody String newPassword) {
        return service.updatePassword(userId, newPassword);
    }

}
