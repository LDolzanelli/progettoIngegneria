package it.unibs.ingsw.destinazioni.ui.rest.controller.test;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;


@RestController
@Profile("test")
@RequestMapping(value = "/api/test")
@RequiredArgsConstructor
public class TestController {
}
