package it.unibs.ingsw.destinazioni.controller;

import it.unibs.ingsw.destinazioni.entity.UserEntity;
import it.unibs.ingsw.destinazioni.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping("{nickname}")
    public UserEntity findUser(@PathVariable("nickname") String nickname) {
        return service.findUser(nickname);
    }

    @PostMapping("{nickname}")
    public UserEntity updateUser(@PathVariable("nickname") String nickname, @RequestBody UserEntity user) {
        return service.updateUser(nickname, user);
    }


}
