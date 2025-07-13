package it.unibs.ingsw.destinazioni.ui.view.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

@Controller
@Slf4j
public class DatePickerController {

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/date-picker")
    public String showDatePicker(@AuthenticationPrincipal UserDetails principal,
                                 @RequestParam(name = "mode", defaultValue = "blocked") String mode,
                                 Model model) {

        String nickname = principal.getUsername();
        String url = "http://localhost:8080/api/";
        String urlId = url + "users/get-id/" + nickname;
        String urlAvailability = url + "volunteer-availability/is-enabled";

        int userId = restTemplate.getForObject(urlId, Integer.class);
        boolean availabilityStatus = restTemplate.getForObject(urlAvailability, Boolean.class);

        model.addAttribute("userId", userId);
        model.addAttribute("mode", mode);
        model.addAttribute("availabilityStatus", availabilityStatus);

        return "date-picker"; 
    }
}