package it.unibs.ingsw.destinazioni.view.controller;

import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/change-credentials")
public class ChangeCredentialsController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String showForm(@AuthenticationPrincipal UserDetails principal, Model model) {
        model.addAttribute("username", principal.getUsername());
        return "change-credentials";
    }

    @PostMapping
    public String updateCredentials(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam String newUsername,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Model model,
            HttpServletRequest request
    ) {
        String currentUsername = principal.getUsername();

        if (newUsername.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
            model.addAttribute("error", "Riempire tutte le caselle");
            return "change-credentials";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Le password non corrispondono");
            return "change-credentials";
        }

        var existing = userService.findByNickname(newUsername);
        if (!newUsername.equals(currentUsername) && existing.isPresent()) {
            model.addAttribute("error", "Username non disponibile");
            return "change-credentials";
        }

        var userOpt = userService.findByNickname(currentUsername);
        if (userOpt.isEmpty()) {
            model.addAttribute("error", "Utente non trovato");
            return "change-credentials";
        }

        User user = userOpt.get();
        user.setNickname(newUsername);
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setFirstLogin(false);
        userService.save(user); // aggiungi il metodo save(User) al servizio

        // Invalida la sessione per forzare il logout
        request.getSession().invalidate();

        return "redirect:/login?logout"; // rimanda al login con messaggio opzionale
    }
}