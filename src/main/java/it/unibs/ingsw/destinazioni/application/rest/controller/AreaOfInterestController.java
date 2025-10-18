package it.unibs.ingsw.destinazioni.application.rest.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.unibs.ingsw.destinazioni.application.port.in.areaofinterest.ManageAreaOfInterestUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.areaofinterest.QueryAreaOfInterestUseCase;
import it.unibs.ingsw.destinazioni.domain.dto.AreasOfInterestDTO;
import it.unibs.ingsw.destinazioni.domain.dto.TownProvinceDTO;
import lombok.RequiredArgsConstructor;

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


    @PostMapping("/addArea")
    public ResponseEntity<Void> addArea(@RequestBody TownProvinceDTO dto) {
        manageAreaOfInterest.addArea(dto.town(), dto.province());
        return ResponseEntity.ok().build();
    }


    @GetMapping("/townProvinceMap")
    public ResponseEntity<Map<String, String>> townProvinceMap() {
        return ResponseEntity.ok(queryAreaOfInterest.townProvinceMap());
    }
}
