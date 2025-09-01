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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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

        int nextMonth = 0;
        int nextYear = 0;
        try {
            ResponseEntity<Integer> monthResponse = restTemplate.getForEntity(
                    "http://localhost:8080/api/visit-plan/next-month", Integer.class);
            nextMonth = monthResponse.getBody();

            ResponseEntity<Integer> yearResponse = restTemplate.getForEntity(
                    "http://localhost:8080/api/visit-plan/next-month-year", Integer.class);
            nextYear = yearResponse.getBody();
        } catch (Exception e) {
            model.addAttribute("error", "Errore nel recuperare mese/anno successivo");
        }

        VisitInformationDTO[] visitPlan = new VisitInformationDTO[0];
        try {
            String url = "http://localhost:8080/api/visit-plan/get-month-visit-plan?month="
                    + nextMonth + "&year=" + nextYear;
            ResponseEntity<VisitInformationDTO[]> response =
                    restTemplate.getForEntity(url, VisitInformationDTO[].class);
            visitPlan = response.getBody();
        } catch (Exception e) {
            model.addAttribute("error", "Errore nel recupero del piano visite");
        }

        Map<String, List<VisitInformationDTO>> visitsByType = Arrays.stream(visitPlan)
                .collect(Collectors.groupingBy(VisitInformationDTO::visitTypeTitle));

        visitsByType.forEach((visitType, visits) ->
                visits.sort(Comparator.comparing(v -> LocalDate.parse(v.date(), DateTimeFormatter.ofPattern("dd/MM/yyyy"))))
        );

        model.addAttribute("visitsByType", visitsByType);
        model.addAttribute("month", nextMonth);
        model.addAttribute("year", nextYear);

        return "view-plan";
    }
}
