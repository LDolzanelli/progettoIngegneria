package it.unibs.ingsw.destinazioni.ui.view.controller;

import it.unibs.ingsw.destinazioni.domain.dto.VisitInformationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class VisitArchiveViewController {

    private final RestTemplate restTemplate;

    @Value("${api.base-url}")
    private String apiBaseUrl;

    @GetMapping("/view-archive")
    public String viewVisitArchive(@AuthenticationPrincipal UserDetails principal, Model model) {

        model.addAttribute("username", principal.getUsername());

        int userId = 0;
        try {
            String url = apiBaseUrl + "/users/get-id/" + principal.getUsername();
            ResponseEntity<Integer> userIdResponse = restTemplate.getForEntity(url, Integer.class);
            userId = userIdResponse.getBody();
        } catch (Exception e) {
            model.addAttribute("error", "Errore nel recupero dello user");
        }
        model.addAttribute("userId", userId);

        VisitInformationDTO[] archiveVisits = new VisitInformationDTO[0];
        try {
            String url = apiBaseUrl + "/visit-plan/get-completed-visits";
            ResponseEntity<VisitInformationDTO[]> response =
                    restTemplate.getForEntity(url, VisitInformationDTO[].class);
            archiveVisits = response.getBody();
        } catch (Exception e) {
            model.addAttribute("error", "Errore nel recupero dell'archivio visite");
        }

        Map<String, List<VisitInformationDTO>> visitsByType = Arrays.stream(archiveVisits)
                .sorted(Comparator.comparing(VisitInformationDTO::visitTypeTitle))
                .collect(Collectors.groupingBy(
                        VisitInformationDTO::visitTypeTitle,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        visitsByType.forEach((visitType, visits) ->
                visits.sort(Comparator.comparing(v ->
                        LocalDate.parse(v.date(), DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                ))
        );

        model.addAttribute("visitsByType", visitsByType);

        return "view-archive";
    }
}
