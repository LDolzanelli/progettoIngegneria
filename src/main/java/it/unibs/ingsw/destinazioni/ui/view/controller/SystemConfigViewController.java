package it.unibs.ingsw.destinazioni.ui.view.controller;

import it.unibs.ingsw.destinazioni.application.port.in.ChangeMaxNumberOfTicketsUseCase;
import it.unibs.ingsw.destinazioni.domain.dto.ChangeMaxNumberTicketsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/system-config")
public class SystemConfigViewController {

    private final ChangeMaxNumberOfTicketsUseCase ticketsUseCase;

    @GetMapping
    public String showConfigPage(Model model) {
        int currentMax = ticketsUseCase.getMaxNumberOfTickets();
        model.addAttribute("oldMax", currentMax);
        model.addAttribute("dto", new ChangeMaxNumberTicketsDTO(currentMax, 0));
        return "system-config";
    }

    @PostMapping
    public String updateMaxTickets(@ModelAttribute("dto") ChangeMaxNumberTicketsDTO dto, Model model) {
        ticketsUseCase.setMaxNumberOfTickets(dto.newMaxNumberTickets());
        return "redirect:/system-config";
    }
}
