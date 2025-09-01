package it.unibs.ingsw.destinazioni.ui.rest.controller;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

import it.unibs.ingsw.destinazioni.ui.rest.mapper.VisitMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.unibs.ingsw.destinazioni.application.port.in.ManageLocationUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.VisitDaysUseCase;
import it.unibs.ingsw.destinazioni.domain.dto.VolunteerVisitSummaryDTO;
import it.unibs.ingsw.destinazioni.domain.model.Visit;
import lombok.RequiredArgsConstructor;
import it.unibs.ingsw.destinazioni.domain.dto.VisitInformationDTO;
import it.unibs.ingsw.destinazioni.domain.model.User;

@RestController
@RequestMapping("/api/visit")
@RequiredArgsConstructor
public class VisitsController {

    private final VisitDaysUseCase visitDaysUseCase;
    private final ManageLocationUseCase locationService;
    private final VisitMapper visitMapper;


    @GetMapping("/list-confirmed/{volunteerNickname}")
    public ResponseEntity<List<VolunteerVisitSummaryDTO>> getConfirmedVisitsForVolunteer(
            @PathVariable String volunteerNickname) {
        try {
            List<Visit> visits = visitDaysUseCase.getConfirmedVisitsPerVolunteer(volunteerNickname);

            List<VolunteerVisitSummaryDTO> dtos = visits.stream()
                    .map(visit -> new VolunteerVisitSummaryDTO(visit.getId(), visit.getDate().toString(),
                            visit.getVisitType().getStartTime().toString(), visit.getVisitType().getTitle(),
                            locationService.getLocationForVisitType(visit.getVisitType()).getName(),
                            visit.getVisitStatus().getItalianName(), visit.visitorsNumber()))
                    .toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    @GetMapping("/details/{visitId}")
    public ResponseEntity<VisitInformationDTO> getVisitDetails(@PathVariable int visitId) {
        try {
            Visit visit = visitDaysUseCase.getVisitById(visitId);
            return ResponseEntity.ok(visitMapper.toVisitInformationDTO(visit));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
