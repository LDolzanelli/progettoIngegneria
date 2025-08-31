package main.java.it.unibs.ingsw.destinazioni.ui.view.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import it.unibs.ingsw.destinazioni.domain.model.User;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.unibs.ingsw.destinazioni.domain.dto.VisitInformationDTO;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class VisitPlanViewController {
    private final RestTemplate restTemplate = new RestTemplate();

//        @GetMapping("/view-plan")
//        public String viewVisitPlan(@AuthenticationPrincipal UserDetails principal, Model model){
//
//            System.out.println("ViewPlanController called!");
//
//            model.addAttribute("username", principal.getUsername());
//            String userRole = "finalUser";
//
//            try {
//                String url = "http://localhost:8080/api/users/get-role/" + principal.getUsername();
//                ResponseEntity<String> role = restTemplate.getForEntity(url, String.class);
//                userRole = role.getBody();
//            } catch (Exception e) {
//                model.addAttribute("error", "Errore nel recupero dello user");
//            }
//
//            model.addAttribute("role", userRole);
//
//            int nextMonth = 0;
//            try{
//                String url = "http://localhost:8080/api/visit-plan/next-month";
//                ResponseEntity<Integer> monthResponse = restTemplate.getForEntity(url, Integer.class);
//                nextMonth = monthResponse.getBody();
//            }catch(Exception e){
//                model.addAttribute("error", "Errore nel recuperare il mese successivo");
//            }
//
//            int year = 0;
//            try{
//                String url = "http://localhost:8080/api/visit-plan/next-month-year";
//                ResponseEntity<Integer> yearResponse = restTemplate.getForEntity(url, Integer.class);
//                year = yearResponse.getBody();
//            }catch(Exception e){
//                model.addAttribute("error", "Errore nel recuperare il mese successivo");
//            }
//
//            VisitInformationDTO[] visitPlan = new VisitInformationDTO[0];
//            try{
//
//                String url = "http://localhost:8080/api/visit-plan/plan?month=" + nextMonth + "&year=" + year;
//                ResponseEntity<VisitInformationDTO[]> response = restTemplate.getForEntity(url, VisitInformationDTO[].class);
//                visitPlan = response.getBody();
//            } catch(Exception e){
//                model.addAttribute("error", "Errore nel recupero del piano visite");
//            }
//
//            if(visitPlan.length == 0)
//                model.addAttribute("visitPlan", List.of());
//            else model.addAttribute("visitPlan", Arrays.asList(visitPlan));
//
//            return "view-plan";
//        }
}