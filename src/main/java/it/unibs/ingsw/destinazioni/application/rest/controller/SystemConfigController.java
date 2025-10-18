package it.unibs.ingsw.destinazioni.application.rest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.unibs.ingsw.destinazioni.application.port.in.configuration.ChangeMaxNumberOfTicketsUseCase;
import it.unibs.ingsw.destinazioni.domain.dto.ChangeMaxNumberTicketsDTO;
import lombok.RequiredArgsConstructor;

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
