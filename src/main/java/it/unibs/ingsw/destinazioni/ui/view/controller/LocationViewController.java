package it.unibs.ingsw.destinazioni.ui.view.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Controller
@RequiredArgsConstructor
public class LocationViewController {

    private final RestTemplate restTemplate = new RestTemplate();

    //controller per la pagina che mostra tutte le visite già presenti
    @GetMapping("/view-locations")
    public String viewLocations(@AuthenticationPrincipal UserDetails principal, Model model) {
        model.addAttribute("username", principal.getUsername());

        //recupera la lista locations dal DB
        try {
            var response = restTemplate.getForObject("http://localhost:8080/api/location/list", Object.class);
            model.addAttribute("locations", response);
        } catch (HttpClientErrorException e) {
            model.addAttribute("error", "Errore nel recupero delle locations");
        }

        return "view-locations";
    }

    //controller per la pagina di aggiunta luogo interesse
    @GetMapping("/add-location")
    public String addLocationForm(@AuthenticationPrincipal UserDetails principal, Model model) {
        model.addAttribute("username", principal.getUsername());

        //la lista delle città disponibili è recuperara dall'area di interesse
        try {
            var response = restTemplate.getForObject("http://localhost:8080/api/area-of-interest/townList", Object.class);
            model.addAttribute("towns", response);
        } catch (HttpClientErrorException e) {
            model.addAttribute("error", "Errore nel recupero delle towns");
        }

        return "add-location";
    }

    //gestione del post per aggiungere un luogo
    @PostMapping("/add-location")
    public String addLocationSubmit(@AuthenticationPrincipal UserDetails principal,
                                    @RequestParam String name,
                                    @RequestParam String description,
                                    @RequestParam String street,
                                    @RequestParam String streetNumber,
                                    @RequestParam String town,
                                    @RequestParam String province,
                                    Model model) {

        //mappa i dati ricevuti in JSON DTO da inviare al backend
        var locationDto = new java.util.HashMap<String, Object>();
        locationDto.put("name", name);
        locationDto.put("description", description);
        locationDto.put("address", java.util.Map.of(
                "street", street,
                "streetNumber", streetNumber,
                "town", town,
                "province", province
        ));
        locationDto.put("visitTypes", java.util.Collections.emptyList());

        try {
            restTemplate.postForEntity("http://localhost:8080/api/location/add", locationDto, Void.class);
            model.addAttribute("success", "Location aggiunta con successo");
        } catch (HttpClientErrorException e) {
            model.addAttribute("error", "Errore nell'aggiunta della location: " + e.getResponseBodyAsString());
            return "add-location";
        }

        return "add-location";
    }
}
