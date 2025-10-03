package it.unibs.ingsw.destinazioni.ui.view.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Controller
@Slf4j
@RequiredArgsConstructor
public class CancelBookingController {

    private final RestTemplate restTemplate;

    @Value("${api.base-url}")
    private String apiBaseUrl;

    @GetMapping("/cancel-booking")
    public String showCancelBookingPage(@AuthenticationPrincipal UserDetails principal, Model model) {
        String nickname = principal.getUsername();

        String urlId = apiBaseUrl + "/users/get-id/" + nickname;

        try {
            int userId = restTemplate.getForObject(urlId, Integer.class);

            model.addAttribute("userId", userId);
            model.addAttribute("username", nickname);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return "cancel-booking";
    }
}
