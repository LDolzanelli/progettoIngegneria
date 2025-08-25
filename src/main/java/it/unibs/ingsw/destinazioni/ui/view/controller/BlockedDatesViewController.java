package it.unibs.ingsw.destinazioni.ui.view.controller;

import it.unibs.ingsw.destinazioni.domain.dto.BlockedDatesDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;

@Controller
@Slf4j
public class BlockedDatesViewController {

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/blocked-dates")
    public String showBlockedDatesForm(Model model) {
        return "blocked-dates"; // Thymeleaf page
    }

    @PostMapping("/blocked-dates")
    public String handleBlockedDatesSubmission(@RequestParam("blockedDates") String blockedDatesString,
            RedirectAttributes redirectAttributes
    ) {
        try {
            var dateList = Arrays.stream(blockedDatesString.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();

            System.out.printf("Date ricevute: {}", dateList);

            BlockedDatesDTO dto = new BlockedDatesDTO(dateList);

            restTemplate.postForObject("http://localhost:8080/api/blocked-dates/set", dto, Void.class);

            redirectAttributes.addFlashAttribute("success", "Date bloccate inviate con successo.");
            return "redirect:/blocked-dates";

        } catch (HttpStatusCodeException e) {
            redirectAttributes.addFlashAttribute("error", "Errore HTTP: " + e.getResponseBodyAsString());
            return "redirect:/blocked-dates";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Errore generico: " + e.getMessage());
            return "redirect:/blocked-dates";
        }
    }
}
