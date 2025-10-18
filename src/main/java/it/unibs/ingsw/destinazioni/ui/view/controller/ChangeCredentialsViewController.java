package it.unibs.ingsw.destinazioni.ui.view.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import it.unibs.ingsw.destinazioni.domain.dto.ChangeCredentialsDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/change-credentials")
public class ChangeCredentialsViewController {

    private final RestTemplate restTemplate;

    @Value("${api.base-url}")
    private String apiBaseUrl;

    @GetMapping
    public String showForm(@AuthenticationPrincipal UserDetails principal,
                           @RequestParam String role,
                           Model model) {
        model.addAttribute("username", principal.getUsername());
        model.addAttribute("role", role);
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
            ResponseEntity<String> response = restTemplate.postForEntity(apiBaseUrl + "/users/change-both-credentials", dto, String.class);

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
            String errorMessage = "Errore durante l'aggiornamento delle credenziali";

            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                errorMessage = e.getResponseBodyAsString();
            }

            model.addAttribute("username", currentUsername);
            model.addAttribute("error", errorMessage);
            return "change-credentials";
        }
    }
}

