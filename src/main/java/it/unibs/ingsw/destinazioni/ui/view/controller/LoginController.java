package it.unibs.ingsw.destinazioni.ui.view.controller;

import it.unibs.ingsw.destinazioni.ui.view.dto.LoginFormDTO;
import it.unibs.ingsw.destinazioni.domain.dto.LoginRequestDTO;
import it.unibs.ingsw.destinazioni.domain.dto.LoginResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
public class LoginController {

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginForm", new LoginFormDTO());
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(
            @ModelAttribute("loginForm") LoginFormDTO form,
            RedirectAttributes redirectAttributes) {

        LoginRequestDTO request = new LoginRequestDTO(form.getNickname(), form.getPassword());

        try {
            LoginResponseDTO response = restTemplate.postForObject(
                    "http://localhost:8080/api/users/login",
                    request,
                    LoginResponseDTO.class
            );

            if (response == null) {
                redirectAttributes.addFlashAttribute("error", "Errore: risposta vuota dal server");
                return "redirect:/login";
            }

            return "redirect:/home";

        } catch (HttpClientErrorException e) {
            redirectAttributes.addFlashAttribute("error", "Login fallito: credenziali errate");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Errore generico: " + e.getMessage());
            return "redirect:/login";
        }
    }
}