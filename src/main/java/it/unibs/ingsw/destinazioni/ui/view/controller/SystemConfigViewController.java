package it.unibs.ingsw.destinazioni.ui.view.controller;

import it.unibs.ingsw.destinazioni.domain.dto.ChangeMaxNumberTicketsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Controller
@RequiredArgsConstructor
@RequestMapping("/system-config")
public class SystemConfigViewController {

    private final RestTemplate restTemplate;

    @Value("${api.base-url}")
    private String apiBaseUrl;

    @GetMapping
    public String showConfigPage(Model model) {
        int currentMax;
        try {
            currentMax = restTemplate.getForObject(apiBaseUrl + "/system-config/getMaxTickets", Integer.class);
        } catch (Exception e) {
            currentMax = 0;
            model.addAttribute("error", "Errore nel recupero della configurazione attuale");
        }

        model.addAttribute("oldMax", currentMax);
        model.addAttribute("dto", new ChangeMaxNumberTicketsDTO(currentMax, 0));
        return "system-config";
    }

    @PostMapping
    public String updateMaxTickets(@ModelAttribute("dto") ChangeMaxNumberTicketsDTO dto, Model model) {
        try {
            restTemplate.postForEntity(apiBaseUrl + "/system-config/changeMaxTickets", dto, Void.class);
        } catch (Exception e) {
            model.addAttribute("error", "Errore durante l'aggiornamento della configurazione");
            return "system-config";
        }
        return "redirect:/system-config";
    }
}
