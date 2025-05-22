package it.unibs.ingsw.destinazioni.ui.view.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/insert-areas-of-interest")
public class InsertAreasController {

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping
    public String showForm(@AuthenticationPrincipal UserDetails principal, Model model) {
        model.addAttribute("username", principal.getUsername());
        return "insert-areas-of-interest"; // pagina Thymeleaf
    }

    @PostMapping
    public String insertAreas(@AuthenticationPrincipal UserDetails principal,
                              @RequestParam List<String> towns,
                              Model model) {

        if (towns == null || towns.isEmpty() || towns.stream().anyMatch(String::isBlank)) {
            model.addAttribute("error", "Inserire almeno una località valida");
            return "insert-areas-of-interest";
        }

        try {
            for (String town : towns) {
                restTemplate.postForEntity("http://localhost:8080/api/area-of-interest/addTown", town, Void.class);
            }
        } catch (HttpClientErrorException e) {
            model.addAttribute("error", "Errore durante l'inserimento: " + e.getResponseBodyAsString());
            return "insert-areas-of-interest";
        }

        model.addAttribute("success", "Località aggiunte correttamente");
        return "insert-areas-of-interest";
    }
}
