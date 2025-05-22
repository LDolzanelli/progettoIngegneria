package it.unibs.ingsw.destinazioni.ui.rest.controller;

import it.unibs.ingsw.destinazioni.application.port.in.ChangeMaxNumberOfTicketsUseCase;
import it.unibs.ingsw.destinazioni.domain.dto.ChangeMaxNumberTicketsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/system-config")
public class SystemConfigController {
    private final ChangeMaxNumberOfTicketsUseCase ticketsUseCase;

    @PostMapping("/changeMaxTickets")
    public ResponseEntity<Void> changeMaxTickets(@RequestBody ChangeMaxNumberTicketsDTO dto) {
        ticketsUseCase.setMaxNumberOfTickets(dto.newMaxNumberTickets());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getMaxTickets")
    public ResponseEntity<Integer> getMaxTickets() {
        return ResponseEntity.ok(ticketsUseCase.getMaxNumberOfTickets());
    }
}
