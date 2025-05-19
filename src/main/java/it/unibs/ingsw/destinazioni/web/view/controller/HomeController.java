package it.unibs.ingsw.destinazioni.web.view.controller;

import it.unibs.ingsw.destinazioni.application.service.UserService;
import it.unibs.ingsw.destinazioni.domain.dto.LoginResponseDTO;
import it.unibs.ingsw.destinazioni.domain.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

@Controller
public class HomeController {

    private final RestTemplate restTemplate = new RestTemplate();


    @GetMapping("/")
    public String home(@AuthenticationPrincipal UserDetails principal, Model model) {
        String nickname = principal.getUsername();

        String url = "http://localhost:8080/api/users/info/" + nickname;
        LoginResponseDTO userInfo;

        try {
            userInfo = restTemplate.getForObject(url, LoginResponseDTO.class);
        } catch (Exception e) {
            throw new IllegalStateException("Errore nel recupero dell'utente: " + nickname, e);
        }

        if (userInfo.firstLogin()) {
            return "redirect:/change-credentials";
        }

        model.addAttribute("username", userInfo.nickname());
        return "home";
    }
}

