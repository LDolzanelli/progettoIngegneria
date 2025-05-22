package it.unibs.ingsw.destinazioni.ui.view.controller;

import it.unibs.ingsw.destinazioni.domain.dto.ChangeCredentialsDTO;
import jakarta.servlet.http.HttpServletRequest;
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
            restTemplate.postForEntity("http://localhost:8080/api/users/change-both-credentials", dto, Void.class);
        } catch (HttpClientErrorException e) {
            model.addAttribute("error", "Errore: " + e.getResponseBodyAsString());
            return "change-credentials";
        }

        request.getSession().invalidate();
        return "redirect:/login?logout";
    }
}
