package it.unibs.ingsw.destinazioni.UI.view.controller;

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

        String url = "http://localhost:8080/api/";
        String urlNickname = url + "users/info/" + nickname;

        LoginResponseDTO userInfo;

        try {
            userInfo = restTemplate.getForObject(urlNickname, LoginResponseDTO.class);
        } catch (Exception e) {
            throw new IllegalStateException("Errore nel recupero dell'utente: " + nickname, e);
        }

        //Al primo login viene controllato il ruolo. Se
        if (userInfo.firstLogin()) {
            if(userInfo.role().equalsIgnoreCase("configurator")) {
                return "redirect:/change-credentials";
            }
            //TODO: redirect:/change-password per i volontari
        }

        //Se l'utente è un configuratore, e non esiste ancora un corpo dati, viene reindirizzato ad una pagina
        //che gli permette di inserire location
        if (userInfo.role().equalsIgnoreCase("configurator")) {
            String urlArea = url + "locations/isEmpty";
            Boolean exists = restTemplate.getForObject(urlArea, Boolean.class);
            if(Boolean.TRUE.equals(restTemplate.getForObject(urlArea, Boolean.class))) {
                return "redirect:/insert-areas-of-interest";
            }
        }

        model.addAttribute("username", userInfo.nickname());
        return "home";
    }
}

