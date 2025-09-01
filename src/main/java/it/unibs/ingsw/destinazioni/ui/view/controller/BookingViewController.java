package it.unibs.ingsw.destinazioni.ui.view.controller;

import it.unibs.ingsw.destinazioni.domain.dto.VisitInformationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequiredArgsConstructor
public class BookingViewController {

    private final RestTemplate restTemplate;

    @GetMapping("/book-visit")
    public String bookVisitForm(@RequestParam int visitId,
                                @AuthenticationPrincipal UserDetails principal,
                                Model model) {
        model.addAttribute("username", principal.getUsername());

        int userId = 0;
        try {
            String urlUser = "http://localhost:8080/api/users/get-id/" + principal.getUsername();
            ResponseEntity<Integer> userIdResponse = restTemplate.getForEntity(urlUser, Integer.class);
            userId = userIdResponse.getBody() != null ? userIdResponse.getBody() : 0;
        } catch (Exception e) {
            model.addAttribute("error", "Errore nel recupero dello user");
        }
        model.addAttribute("userId", userId);

        try {
            String urlVisit = "http://localhost:8080/api/visit/details/" + visitId;
            ResponseEntity<VisitInformationDTO> response = restTemplate.getForEntity(urlVisit, VisitInformationDTO.class);
            VisitInformationDTO visit = response.getBody();
            model.addAttribute("visit", visit);
        } catch (Exception e) {
            model.addAttribute("error", "Errore nel recupero dei dettagli della visita");
            return "book-visit";
        }

        int maxTickets = 1;
        try {
            String urlMax = "http://localhost:8080/api/system-config/getMaxTickets";
            ResponseEntity<Integer> response = restTemplate.getForEntity(urlMax, Integer.class);
            maxTickets = response.getBody() != null ? response.getBody() : 1;
        } catch (Exception e) {
            model.addAttribute("error", "Errore nel recupero del numero massimo di biglietti");
        }

        String urlVisit = "http://localhost:8080/api/visit/details/" + visitId;
        VisitInformationDTO visit = restTemplate.getForEntity(urlVisit, VisitInformationDTO.class).getBody();
        int maxParticipants = visit.maxParticipants();
        int currentParticipants = visit.numberOfParticipants();
        int maxTicketsBeforeFull = maxParticipants - currentParticipants;

        if (maxTickets > maxTicketsBeforeFull) maxTickets = maxTicketsBeforeFull;
        model.addAttribute("maxTickets", maxTickets);

        return "book-visit";
    }

    @PostMapping("/book-visit")
    public String bookVisitSubmit(@RequestParam int visitId,
                                  @RequestParam int userId,
                                  @RequestParam List<String> visitorsNames,
                                  RedirectAttributes redirectAttributes) {
        try {
            String urlVisit = "http://localhost:8080/api/visit/details/" + visitId;
            VisitInformationDTO visit = restTemplate.getForEntity(urlVisit, VisitInformationDTO.class).getBody();
            int maxParticipants = visit.maxParticipants();
            int currentParticipants = visit.numberOfParticipants();

            if (currentParticipants + visitorsNames.size() > maxParticipants) {
                redirectAttributes.addFlashAttribute("error", "Numero massimo di partecipanti superato!");
                return "redirect:/book-visit?visitId=" + visitId;
            }

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("visitId", visitId);
            requestBody.put("userId", userId);
            requestBody.put("visitorsNames", visitorsNames);

            restTemplate.postForEntity("http://localhost:8080/api/booking/book", requestBody, Void.class);
            redirectAttributes.addFlashAttribute("success", "Prenotazione effettuata con successo!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Errore durante la prenotazione: " + e.getMessage());
        }

        return "redirect:/view-plan";
    }
}