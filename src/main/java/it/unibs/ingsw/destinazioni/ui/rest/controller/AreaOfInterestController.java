package it.unibs.ingsw.destinazioni.ui.rest.controller;

import it.unibs.ingsw.destinazioni.application.port.in.ManageAreaOfInterestUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.QueryAreaOfInterestUseCase;
import it.unibs.ingsw.destinazioni.domain.dto.AreasOfInterestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/area-of-interest")
@RequiredArgsConstructor
public class AreaOfInterestController {
    private final ManageAreaOfInterestUseCase manageAreaOfInterest;
    private final QueryAreaOfInterestUseCase queryAreaOfInterest;

    @GetMapping("/isEmpty")
    public ResponseEntity<Boolean> isEmpty() {
        return ResponseEntity.ok(queryAreaOfInterest.isEmpty());
    }

    @GetMapping("/townList")
    public ResponseEntity<AreasOfInterestDTO> townList() {
        AreasOfInterestDTO dto = new AreasOfInterestDTO(queryAreaOfInterest.townList());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/addTown")
    public ResponseEntity<Void> addTown(@RequestBody String town) {
        manageAreaOfInterest.addTown(town);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/removeTown")
    public ResponseEntity<Void> removeTown(@RequestBody String town) {
        manageAreaOfInterest.removeTown(town);
        return ResponseEntity.ok().build();
    }

}