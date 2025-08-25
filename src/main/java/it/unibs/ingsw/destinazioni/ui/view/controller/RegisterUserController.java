package it.unibs.ingsw.destinazioni.ui.view.controller;

import it.unibs.ingsw.destinazioni.domain.dto.LoginResponseDTO;
import it.unibs.ingsw.destinazioni.domain.dto.RegisterUserDTO;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Controller
@RequiredArgsConstructor
@PermitAll
@RequestMapping("/register-user")
public class RegisterUserController {

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping
    public String showForm(@AuthenticationPrincipal UserDetails principal, Model model) {
        boolean isConfigurator = principal != null && principal.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_CONFIGURATOR"));

        model.addAttribute("isConfigurator", isConfigurator);
        model.addAttribute("username", principal != null ? principal.getUsername() : null);
        return "register-user";
    }

    @PostMapping
    public String register(@AuthenticationPrincipal UserDetails principal,
                           @RequestParam String nickname,
                           @RequestParam String password,
                           @RequestParam String passwordConfirm,
                           Model model) {
        String authNickname = principal.getUsername();
        System.out.println("USERNAMEEEE " + authNickname);
        LoginResponseDTO userInfo;

        try {
            userInfo = restTemplate.getForObject("http://localhost:8080/api/users/info/" + authNickname, LoginResponseDTO.class);
        } catch (Exception e) {
            throw new IllegalStateException("Errore nel recupero dell'utente: " + nickname, e);
        }

        if (!password.equals(passwordConfirm)) {
            model.addAttribute("error", "Le password non coincidono!");
            return "register-user";
        }

        boolean isConfigurator = userInfo.role().equalsIgnoreCase("configurator");

        String role = isConfigurator ? "volunteer" : "finalUser";

        RegisterUserDTO dto = new RegisterUserDTO(nickname, password, role);

        try {
            restTemplate.postForEntity("http://localhost:8080/api/users/register", dto, Void.class);
        } catch (HttpClientErrorException e) {
            model.addAttribute("error", "Errore durante la registrazione: " + e.getResponseBodyAsString());
            model.addAttribute("isConfigurator", isConfigurator);
            return "register-user";
        }

        model.addAttribute("success", "Registrazione avvenuta con successo!");
        model.addAttribute("isConfigurator", isConfigurator);
        return "register-user";
    }
}
