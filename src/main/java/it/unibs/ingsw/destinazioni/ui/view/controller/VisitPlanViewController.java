package main.java.it.unibs.ingsw.destinazioni.ui.view.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;

import it.unibs.ingsw.destinazioni.domain.dto.VisitInformationDTO;

import java.util.Arrays;
import java.util.List;

public class VisitPlanViewController {
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/view-plan")
    public String viewVisitPlan(@AuthenticationPrincipal UserDetails principal, Model model){

        model.addAttribute("username", principal.getUsername());
        String userRole = "finalUser";

        try {
            String url = "http://localhost:8080/api/users/get-role/" + principal.getUsername();
            ResponseEntity<String> role = restTemplate.getForEntity(url, String.class);
            userRole = role.getBody();
        } catch (Exception e) {
            model.addAttribute("error", "Errore nel recupero dello user");
        }

        model.addAttribute("role", userRole);

        VisitInformationDTO[] visitPlan = new VisitInformationDTO[0];
        try{

            String url = "http://localhost:8080/api/visit-plan/plan";
            ResponseEntity<VisitInformationDTO[]> response = restTemplate.getForEntity(url, VisitInformationDTO[].class);
            visitPlan = response.getBody();
        } catch(Exception e){
            model.addAttribute("error", "Errore nel recupero del piano visite");
        }

        if(visitPlan.length == 0)
            model.addAttribute("visitPlan", List.of());
        else model.addAttribute("visitPlan", Arrays.asList(visitPlan));

        return "view-plan";
    }
}
