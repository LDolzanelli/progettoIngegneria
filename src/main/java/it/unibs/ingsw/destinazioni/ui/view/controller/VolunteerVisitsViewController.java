package it.unibs.ingsw.destinazioni.ui.view.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import it.unibs.ingsw.destinazioni.domain.dto.VisitInformationDTO;
import it.unibs.ingsw.destinazioni.domain.dto.VolunteerVisitSummaryDTO;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class VolunteerVisitsViewController {

    private final RestTemplate restTemplate;

    @Value("${api.base-url}")
    private String apiBaseUrl;

    @GetMapping("/confirmed-visits")
    public String confirmedVisits(@RequestParam String volunteerNickname, Model model) {
        String url = apiBaseUrl + "/visit/list-confirmed/" + volunteerNickname;
        List<VolunteerVisitSummaryDTO> visits;

        try {
            visits = List.of(restTemplate.getForObject(url, VolunteerVisitSummaryDTO[].class));
        } catch (Exception e) {
            throw new IllegalStateException("Errore nel recupero delle visite confermate per: " + volunteerNickname, e);
        }

        model.addAttribute("visits", visits);
        model.addAttribute("volunteerNickname", volunteerNickname);
        return "confirmed-visits";
    }


    @GetMapping("/visit-details/{visitId}")
    public String visitDetails(@PathVariable Integer visitId, Model model) {
        String url = apiBaseUrl + "/visit/details/" + visitId;
        VisitInformationDTO visit;

        try {
            visit = restTemplate.getForObject(url, VisitInformationDTO.class);
        } catch (Exception e) {
            throw new IllegalStateException("Errore nel recupero dettagli visita ID: " + visitId, e);
        }

        model.addAttribute("visit", visit);
        return "visit-details";
    }
}
