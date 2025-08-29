package it.unibs.ingsw.destinazioni.ui.view.controller;

import java.time.LocalDate;
import java.time.Month;

import it.unibs.ingsw.destinazioni.config.AdjustableClock;
import it.unibs.ingsw.destinazioni.domain.dto.LoginResponseDTO;

import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final AdjustableClock clock;

    @GetMapping("/")
    public String home(@AuthenticationPrincipal UserDetails principal, Model model) {
        String nickname = principal.getUsername();

        String url = "http://localhost:8080/api/";
        String urlNickname = url + "users/info/" + nickname;
        String urlId = url + "/users/get-id/" + nickname;

        LoginResponseDTO userInfo;
        int userId;

        try {
            userInfo = restTemplate.getForObject(urlNickname, LoginResponseDTO.class);
        } catch (Exception e) {
            throw new IllegalStateException("Errore nel recupero dell'utente: " + nickname, e);
        }

        try {
            userId = restTemplate.getForObject(urlId, Integer.class);
        } catch (Exception e) {
            throw new IllegalStateException("Errore nel recupero dell'utente: " + nickname, e);
        }

        assert userInfo != null;
        model.addAttribute("username", userInfo.nickname());
        model.addAttribute("userId", userId);
        model.addAttribute("role", userInfo.role());

        LocalDate today = LocalDate.now(clock);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        model.addAttribute("currentDate", today.format(formatter));

        // Se l'utente è un configuratore, e non esiste ancora un corpo dati, viene reindirizzato ad una
        // pagina che gli permette di inserire location
        if (userInfo.role().equalsIgnoreCase("configurator")) {
            String urlArea = url + "area-of-interest/isEmpty";
            Boolean exists = restTemplate.getForObject(urlArea, Boolean.class);
            if (Boolean.TRUE.equals(exists)) {
                return "redirect:/insert-areas-of-interest";
            }
        }

        if (userInfo.role().equalsIgnoreCase("configurator")) {
            // check corpo dati
            String urlArea = url + "area-of-interest/isEmpty";
            Boolean exists = restTemplate.getForObject(urlArea, Boolean.class);
            if (Boolean.TRUE.equals(exists)) {
                return "redirect:/insert-areas-of-interest";
            }

            // controlli pulsanti
            String canEnableUrl = url + "volunteer-availability/can-enable";
            String canDisableUrl = url + "volunteer-availability/can-disable";

            Boolean canEnable = restTemplate.getForObject(canEnableUrl, Boolean.class);
            Boolean canDisable = restTemplate.getForObject(canDisableUrl, Boolean.class);
            Boolean canCreateVisitPlan = restTemplate.getForObject(url + "visit-plan/can-create", Boolean.class);


            Integer monthToUpdate =
                    restTemplate.getForObject(url + "volunteer-availability/month-to-enable", Integer.class);
            Integer monthToDisable =
                    restTemplate.getForObject(url + "volunteer-availability/month-to-disable", Integer.class);
            Integer monthToCreate = restTemplate.getForObject(url + "visit-plan/next-month", Integer.class);


            model.addAttribute("canCreateVisitPlan", canCreateVisitPlan);
            model.addAttribute("canEnableAvailability", canEnable);
            model.addAttribute("canDisableAvailability", canDisable);
            model.addAttribute("enableMonthLabel",
                    Month.of(monthToUpdate).getDisplayName(TextStyle.FULL, Locale.ITALIAN));
            model.addAttribute("disableMonthLabel",
                    Month.of(monthToDisable).getDisplayName(TextStyle.FULL, Locale.ITALIAN));
            model.addAttribute("createVisitPlanLabel",
                    Month.of(monthToCreate).getDisplayName(TextStyle.FULL, Locale.ITALIAN));
        }


        return "/home";
    }
}

