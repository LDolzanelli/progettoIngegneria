package it.unibs.ingsw.destinazioni.ui.view.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Controller
@RequiredArgsConstructor
public class LocationViewController {

    private final RestTemplate restTemplate = new RestTemplate();

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

        boolean canAddVisitTypes = Boolean.TRUE.equals(
                restTemplate.getForObject("http://localhost:8080/api/visit-type/modification-state-active", Boolean.class));
        model.addAttribute("canAddVisits", canAddVisitTypes);


        return "view-locations";
    }

    @GetMapping("/townProvinceMap")
    @ResponseBody
    public Object townProvinceMap() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject("http://localhost:8080/api/area-of-interest/townProvinceMap", Object.class);
    }

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

    @PostMapping("/add-location")
    public String addLocationSubmit(@AuthenticationPrincipal UserDetails principal,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String street,
            @RequestParam String streetNumber,
            @RequestParam String town,
            Model model) {

        RestTemplate restTemplate = new RestTemplate();

        // la provincia viene recuperata dalla map
		String province;
		try {
			var townProvinceMap = restTemplate.getForObject(
                    "http://localhost:8080/api/area-of-interest/townProvinceMap", java.util.Map.class);
			province = (String) townProvinceMap.get(town);
		} catch (NullPointerException e) {
			throw new RuntimeException(e);
		}


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
