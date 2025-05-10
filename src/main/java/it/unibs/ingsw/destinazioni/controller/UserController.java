package it.unibs.ingsw.destinazioni.controller;

import it.unibs.ingsw.destinazioni.entity.User;
import it.unibs.ingsw.destinazioni.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping
    public User addUser(@RequestBody User user) {
        return service.createUser(user);
    }
}
