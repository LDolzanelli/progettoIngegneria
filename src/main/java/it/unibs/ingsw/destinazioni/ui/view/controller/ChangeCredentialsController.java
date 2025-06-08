package it.unibs.ingsw.destinazioni.ui.view.controller;

import it.unibs.ingsw.destinazioni.domain.dto.ChangeCredentialsDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/change-credentials")
public class ChangeCredentialsController {

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping
    public String showForm(@AuthenticationPrincipal UserDetails principal, Model model) {
        model.addAttribute("username", principal.getUsername());
        return "change-credentials";
    }


    @PostMapping
    public String updateCredentials(@AuthenticationPrincipal UserDetails principal, @RequestParam String newUsername,
            @RequestParam String oldPassword, @RequestParam String newPassword, @RequestParam String confirmPassword,
            Model model, HttpServletRequest request) {

        String currentUsername = principal.getUsername();

        if (newUsername.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
            model.addAttribute("error", "Riempire tutte le caselle");
            return "change-credentials";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Le password non corrispondono");
            return "change-credentials";
        }

        var dto = new ChangeCredentialsDTO(currentUsername, newUsername, oldPassword, newPassword);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8080/api/users/change-both-credentials", dto, String.class);

            System.out.println("ERRORE = " + response + " FINE");
            if (response.getStatusCode().is2xxSuccessful()) {
                request.getSession().invalidate();
                return "redirect:/login?logout";
            } else {
                model.addAttribute("username", currentUsername);
                model.addAttribute("error", "Errore durante l'aggiornamento delle credenziali");
                return "change-credentials";
            }

        } catch (HttpClientErrorException e) {
            //messaggio di default
            String errorMessage = "Errore durante l'aggiornamento delle credenziali";

            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                try {
                    //Response body come messaggio di errore
                    errorMessage = e.getResponseBodyAsString();
                } catch (Exception ex) {
                    //default
                }
            }

            model.addAttribute("username", currentUsername);
            model.addAttribute("error", errorMessage);
            return "change-credentials";
        }
    }
}

