package it.unibs.ingsw.destinazioni.ui.view.controller;

import it.unibs.ingsw.destinazioni.domain.dto.LoginResponseDTO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

@Controller
public class HomeController {

  private final RestTemplate restTemplate = new RestTemplate();

  @GetMapping({ "/", "/home" })
  public String home(@AuthenticationPrincipal UserDetails principal, Model model) {
    String nickname = principal.getUsername();

    String url = "http://localhost:8080/api/";
    String urlNickname = url + "users/info/" + nickname;
    String urlId = url + "/users/get-id/" + nickname;

    LoginResponseDTO userInfo;
    int userId;

    try {
      userInfo = restTemplate.getForObject(urlNickname, LoginResponseDTO.class);
    } catch (Exception e) {
      throw new IllegalStateException("Errore nel recupero dell'utente: " + nickname, e);
    }

    try {
      userId = restTemplate.getForObject(urlId, Integer.class);
    } catch (Exception e) {
      throw new IllegalStateException("Errore nel recupero dell'utente: " + nickname, e);
    }

    assert userInfo != null;
    model.addAttribute("username", userInfo.nickname());
    model.addAttribute("userId", userId);
    model.addAttribute("role", userInfo.role());

    // Se l'utente è un configuratore, e non esiste ancora un corpo dati, viene
    // reindirizzato ad una pagina
    // che gli permette di inserire location
    if (userInfo.role().equalsIgnoreCase("configurator")) {
      String urlArea = url + "area-of-interest/isEmpty";
      Boolean exists = restTemplate.getForObject(urlArea, Boolean.class);
      if (Boolean.TRUE.equals(exists)) {
        return "redirect:/insert-areas-of-interest";
      }
    }

    return "/home";
  }
}
