package it.unibs.ingsw.destinazioni.controller;

import it.unibs.ingsw.destinazioni.entity.UserEntity;
import it.unibs.ingsw.destinazioni.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@Profile("test")
@RequestMapping(value = "/api/test")
@RequiredArgsConstructor
public class TestController {



}
