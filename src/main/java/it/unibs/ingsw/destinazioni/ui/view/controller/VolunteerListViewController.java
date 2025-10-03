package it.unibs.ingsw.destinazioni.ui.view.controller;

import it.unibs.ingsw.destinazioni.domain.dto.VolunteerWithVisitsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class VolunteerListViewController {

    private final RestTemplate restTemplate;

    @Value("${api.base-url}")
    private String apiBaseUrl;

    @GetMapping("/view-volunteers")
    public String viewVolunteers(@AuthenticationPrincipal UserDetails principal, Model model) {
        model.addAttribute("username", principal.getUsername());

        try {
            String url = apiBaseUrl + "/users/volunteers-with-visits";
            ResponseEntity<VolunteerWithVisitsDTO[]> response = restTemplate.getForEntity(url, VolunteerWithVisitsDTO[].class);
            List<VolunteerWithVisitsDTO> volunteers = Arrays.asList(response.getBody());

            model.addAttribute("volunteers", volunteers);

            boolean canAddVisitTypes = Boolean.TRUE.equals(
                    restTemplate.getForObject(apiBaseUrl + "/visit-type/modification-state-active", Boolean.class));
            model.addAttribute("canAddVisits", canAddVisitTypes);

        } catch (HttpClientErrorException e) {
            model.addAttribute("error", "Errore nel recupero dei volontari con visite");
            model.addAttribute("volunteers", List.of());
        }

        return "view-volunteers";
    }
}
