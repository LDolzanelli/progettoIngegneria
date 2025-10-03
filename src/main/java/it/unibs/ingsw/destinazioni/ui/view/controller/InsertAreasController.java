package it.unibs.ingsw.destinazioni.ui.view.controller;

import it.unibs.ingsw.destinazioni.domain.dto.TownProvinceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    private final RestTemplate restTemplate;

    @Value("${api.base-url}")
    private String apiBaseUrl;

    @GetMapping
    public String showForm(@AuthenticationPrincipal UserDetails principal, Model model) {
        model.addAttribute("username", principal.getUsername());
        return "insert-areas-of-interest";
    }

    @PostMapping
    public String insertAreas(@AuthenticationPrincipal UserDetails principal,
            @RequestParam List<String> towns,
            @RequestParam List<String> provinces,
            Model model) {

        if (towns == null || towns.isEmpty() || towns.stream().anyMatch(String::isBlank) ||
                provinces == null || provinces.size() != towns.size() || provinces.stream().anyMatch(String::isBlank)) {
            model.addAttribute("error", "Inserire almeno una località e la relativa provincia");
            return "insert-areas-of-interest";
        }

        try {
            for (int i = 0; i < towns.size(); i++) {
                TownProvinceDTO dto = new TownProvinceDTO(towns.get(i), provinces.get(i));
                restTemplate.postForEntity(apiBaseUrl + "/area-of-interest/addArea", dto, Void.class);
            }
        } catch (HttpClientErrorException e) {
            model.addAttribute("error", "Errore durante l'inserimento: " + e.getResponseBodyAsString());
            return "insert-areas-of-interest";
        }

        model.addAttribute("success", "Località aggiunte correttamente");
        return "insert-areas-of-interest";
    }
}

