package it.unibs.ingsw.destinazioni.ui.view.controller;

import it.unibs.ingsw.destinazioni.domain.dto.VisitInformationDTO;
import lombok.RequiredArgsConstructor;
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
public class VisitPlanViewController {

    private final RestTemplate restTemplate;

    @GetMapping("/view-plan")
    public String viewVisitPlan(@AuthenticationPrincipal UserDetails principal, Model model) {

        model.addAttribute("username", principal.getUsername());

        int userId = 0;
        try {
            String url = "http://localhost:8080/api/users/get-id/" + principal.getUsername();
            ResponseEntity<Integer> userIdResponse = restTemplate.getForEntity(url, Integer.class);
            userId = userIdResponse.getBody();
        } catch (Exception e) {
            model.addAttribute("error", "Errore nel recupero dello user");
        }
        model.addAttribute("userId", userId);

        VisitInformationDTO[] visitPlan = new VisitInformationDTO[0];
        try {
            String url = "http://localhost:8080/api/visit-plan/get-visit-plan-after-today";
            ResponseEntity<VisitInformationDTO[]> response =
                    restTemplate.getForEntity(url, VisitInformationDTO[].class);
            visitPlan = response.getBody();
        } catch (Exception e) {
            model.addAttribute("error", "Errore nel recupero del piano visite");
        }

        Map<String, List<VisitInformationDTO>> visitsByType = Arrays.stream(visitPlan)
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

        return "view-plan";
    }
}
