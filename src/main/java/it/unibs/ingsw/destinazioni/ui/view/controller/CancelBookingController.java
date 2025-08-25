package it.unibs.ingsw.destinazioni.ui.view.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.client.RestTemplate;

@Controller
@Slf4j
public class CancelBookingController {

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/cancel-booking")
    public String showCancelBookingPage(@AuthenticationPrincipal UserDetails principal, Model model) {
        String nickname = principal.getUsername();

        String url = "http://localhost:8080/api/";
        String urlId = url + "users/get-id/" + nickname;

        int userId = restTemplate.getForObject(urlId, Integer.class);

        model.addAttribute("userId", userId);
        model.addAttribute("username", nickname);

        return "cancel-booking";
    }
}
