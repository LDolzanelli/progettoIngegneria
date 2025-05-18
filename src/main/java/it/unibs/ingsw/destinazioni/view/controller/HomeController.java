package it.unibs.ingsw.destinazioni.view.controller;

import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.services.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(@AuthenticationPrincipal UserDetails principal, Model model) {
        String nickname = principal.getUsername();

        User user = userService.findByNickname(nickname)
                .orElseThrow(() -> new IllegalStateException("Utente non trovato dopo login: " + nickname));

        if (Boolean.TRUE.equals(user.isFirstLogin())) {
            return "redirect:/change-credentials";
        }

        model.addAttribute("username", nickname);
        return "home";
    }
}
