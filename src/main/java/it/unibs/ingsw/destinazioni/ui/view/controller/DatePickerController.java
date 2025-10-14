package it.unibs.ingsw.destinazioni.ui.view.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

@Controller
@Slf4j
@RequiredArgsConstructor
public class DatePickerController {

    private final RestTemplate restTemplate;

    @Value("${api.base-url}")
    private String apiBaseUrl;

    @GetMapping("/date-picker")
    public String showDatePicker(@AuthenticationPrincipal UserDetails principal,
                                 @RequestParam(name = "mode", defaultValue = "blocked") String mode,
                                 Model model) {

        String nickname = principal.getUsername();
        String urlId = apiBaseUrl + "/users/get-id/" + nickname;
        String urlAvailability = apiBaseUrl + "/volunteer-availability/is-enabled";

        try {
            int userId = restTemplate.getForObject(urlId, Integer.class);
            boolean availabilityStatus = restTemplate.getForObject(urlAvailability, Boolean.class);

            model.addAttribute("userId", userId);
            model.addAttribute("mode", mode);
            model.addAttribute("availabilityStatus", availabilityStatus);

            model.addAttribute("apiBaseUrl", apiBaseUrl);

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return "date-picker"; 
    }
}