package it.unibs.ingsw.destinazioni.ui.view.controller;

import it.unibs.ingsw.destinazioni.domain.dto.VisitTypeDTO;
import it.unibs.ingsw.destinazioni.domain.dto.VolunteerDTO;
import it.unibs.ingsw.destinazioni.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//da rimuovere
import it.unibs.ingsw.destinazioni.domain.dto.VisitInformationDTO;

@Controller
@RequiredArgsConstructor
public class VisitTypeViewController {

    private final RestTemplate restTemplate;
    private final Clock clock;

    @Value("${api.base-url}")
    private String apiBaseUrl;

    @GetMapping("/view-visittype")
    public String viewVisitTypes(@RequestParam(required = false) Long locationId,
            @RequestParam(required = false) Integer volunteerId, @AuthenticationPrincipal UserDetails principal,
            Model model) {

        model.addAttribute("username", principal.getUsername());
        String userRole = "finalUser";

        try {
            String url = apiBaseUrl + "/users/get-role/" + principal.getUsername();
            ResponseEntity<String> role = restTemplate.getForEntity(url, String.class);
            userRole = role.getBody();
        } catch (Exception e) {
            model.addAttribute("error", "Errore nel recupero dello user");
        }

        model.addAttribute("role", userRole);

        boolean canAddVisitTypes = Boolean.TRUE.equals(
                restTemplate.getForObject(apiBaseUrl + "/visit-type/modification-state-active", Boolean.class));
        model.addAttribute("canAddVisits", canAddVisitTypes);

        try {
            VisitTypeDTO[] visitTypes;

            if (volunteerId != null) {
                // caso volontario: carica le visite a lui assegnate
                String url = apiBaseUrl + "/visit-type/volunteer/" + volunteerId;
                ResponseEntity<VisitTypeDTO[]> response = restTemplate.getForEntity(url, VisitTypeDTO[].class);
                visitTypes = response.getBody();

            } else if (locationId != null) {
                // caso normale: carica le visite associate ad un luogo
                String url = apiBaseUrl + "/visit-type/list/" + locationId;
                ResponseEntity<VisitTypeDTO[]> response = restTemplate.getForEntity(url, VisitTypeDTO[].class);
                visitTypes = response.getBody();

            } else {
                // caso in cui non viene fornito niente: mostra lista vuota
                visitTypes = new VisitTypeDTO[0];
            }

            model.addAttribute("visitTypes", visitTypes != null ? Arrays.asList(visitTypes) : List.of());

        } catch (Exception e) {
            model.addAttribute("error", "Errore nel recupero dei tipi di visita");
            model.addAttribute("visitTypes", List.of());
        }

        return "view-visittype";
    }


    @GetMapping("/add-visittype")
    public String addVisitTypeForm(@AuthenticationPrincipal UserDetails principal, Model model) {
        model.addAttribute("username", principal.getUsername());
        LocalDate currentDate = LocalDate.now(clock);
        model.addAttribute("currentDate", currentDate);

        try {
            String url = apiBaseUrl + "/users/list_volunteers";
            ResponseEntity<VolunteerDTO[]> response = restTemplate.getForEntity(url, VolunteerDTO[].class);
            List<VolunteerDTO> volunteers = Arrays.asList(response.getBody());

            model.addAttribute("volunteers", volunteers);
        } catch (HttpClientErrorException e) {
            model.addAttribute("error", "Errore nel recupero dei volontari");
            model.addAttribute("volunteers", List.of());
        }

        return "add-visittype";
    }


    @PostMapping("/add-visittype")
    public String addVisitTypeSubmit(@AuthenticationPrincipal UserDetails principal, @RequestParam String title,
            @RequestParam String description, @RequestParam String meetingPoint, @RequestParam String startDate,     // Formato
                                                                                                                     // ISO:
                                                                                                                     // "2025-06-01"
            @RequestParam String endDate, @RequestParam String startTime,     // Formato ISO: "14:00:00"
            @RequestParam int duration, @RequestParam int minParticipants, @RequestParam int maxParticipants,
            @RequestParam boolean isFree, @RequestParam List<String> daysOfWeek, @RequestParam List<String> volunteers,
            Model model) {

        model.addAttribute("username", principal.getUsername());

        Map<String, Object> visitTypeDto = new HashMap<>();
        visitTypeDto.put("title", title);
        visitTypeDto.put("description", description);
        visitTypeDto.put("meetingPoint", meetingPoint);
        visitTypeDto.put("startDate", startDate);
        visitTypeDto.put("endDate", endDate);
        visitTypeDto.put("startTime", startTime);
        visitTypeDto.put("duration", duration);
        visitTypeDto.put("minParticipants", minParticipants);
        visitTypeDto.put("maxParticipants", maxParticipants);
        visitTypeDto.put("isFree", isFree);
        visitTypeDto.put("daysOfWeek", daysOfWeek);
        visitTypeDto.put("volunteers", volunteers);

        try {
            restTemplate.postForEntity(apiBaseUrl + "/visit-type/add", visitTypeDto, Void.class);
            model.addAttribute("success", "Tipo di visita aggiunto con successo!");
        } catch (HttpClientErrorException e) {
            model.addAttribute("error", "Errore durante l'aggiunta del tipo di visita: " + e.getResponseBodyAsString());
            return "add-visittype";
        }

        return "add-visittype";
    }


    @GetMapping("/add-volunteers")
    public String addVolunteersPage(@RequestParam int visitTypeId, Model model) {
        try {
            String urlAssigned = apiBaseUrl + "/visit-type/" + visitTypeId;
            ResponseEntity<VisitTypeDTO> response = restTemplate.getForEntity(urlAssigned, VisitTypeDTO.class);
            VisitTypeDTO visitType = response.getBody();

            model.addAttribute("visitTypeId", visitTypeId);
            model.addAttribute("assignedVolunteers", visitType.volunteers());

            String urlAll = apiBaseUrl + "/users/list_volunteers";
            ResponseEntity<VolunteerDTO[]> allResponse = restTemplate.getForEntity(urlAll, VolunteerDTO[].class);
            List<VolunteerDTO> allVolunteers = Arrays.asList(allResponse.getBody());

            List<VolunteerDTO> availableVolunteers =
                    allVolunteers.stream().filter(v -> !visitType.volunteers().contains(v.nickname())).toList();

            model.addAttribute("allVolunteers", availableVolunteers);

        } catch (Exception e) {
            model.addAttribute("error", "Errore nel recupero dei volontari");
            model.addAttribute("assignedVolunteers", List.of());
            model.addAttribute("allVolunteers", List.of());
        }
        return "add-volunteers";
    }



    @PostMapping("/add-volunteers")
    public String addVolunteerSubmit(@RequestParam int visitTypeId, @RequestParam String volunteerNickname,
            RedirectAttributes redirectAttributes) {
        try {
            String url = apiBaseUrl + "/visit-type/add-volunteer/" + visitTypeId + "/" + volunteerNickname;
            restTemplate.postForEntity(url, null, Void.class);

            redirectAttributes.addFlashAttribute("success",
                    "Volontario '" + volunteerNickname + "' aggiunto con successo!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Errore durante l'aggiunta del volontario: " + e.getMessage());
        }
        return "redirect:/add-volunteers?visitTypeId=" + visitTypeId;
    }
}
