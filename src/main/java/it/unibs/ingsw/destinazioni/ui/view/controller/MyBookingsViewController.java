package it.unibs.ingsw.destinazioni.ui.view.controller;

import it.unibs.ingsw.destinazioni.domain.dto.BookingInformationDTO;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MyBookingsViewController {

    private final RestTemplate restTemplate;

    @Value("${api.base-url}")
    private String apiBaseUrl;

    @GetMapping("/my-bookings")
    public String viewMyBookings(@AuthenticationPrincipal UserDetails principal, Model model) {
        model.addAttribute("username", principal.getUsername());

        int userId = 0;
        try {
            String urlId = apiBaseUrl + "/users/get-id/" + principal.getUsername();
            ResponseEntity<Integer> idResponse = restTemplate.getForEntity(urlId, Integer.class);
            userId = idResponse.getBody() != null ? idResponse.getBody() : 0;
        } catch (Exception e) {
            model.addAttribute("error", "Errore nel recupero dell'ID utente");
            return "my-bookings";
        }

        model.addAttribute("userId", userId);

        List<BookingInformationDTO> myBookings;
        Map<String, VisitInformationDTO> visitInformationDTOSwithCode = new LinkedHashMap<>();
        try {
            String urlBookings = apiBaseUrl + "/booking/my-bookings/" + userId;
            ResponseEntity<BookingInformationDTO[]> response = restTemplate.getForEntity(urlBookings, BookingInformationDTO[].class);
            myBookings = response.getBody() != null ? Arrays.asList(response.getBody()) : List.of();
        } catch (Exception e) {
            model.addAttribute("error", "Errore nel recupero delle prenotazioni");
            myBookings = List.of();
        }

        myBookings.forEach(b -> {
            visitInformationDTOSwithCode.put(b.bookingCode(), b.visitInfo());
        });

        model.addAttribute("myBookings", visitInformationDTOSwithCode);

        return "my-bookings";
    }
}