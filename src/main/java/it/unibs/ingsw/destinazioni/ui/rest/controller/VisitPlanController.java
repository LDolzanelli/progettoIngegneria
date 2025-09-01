package it.unibs.ingsw.destinazioni.ui.rest.controller;

import java.util.List;

import it.unibs.ingsw.destinazioni.ui.rest.mapper.VisitMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import it.unibs.ingsw.destinazioni.application.port.in.VisitPlanUseCase;
import it.unibs.ingsw.destinazioni.domain.model.Visit;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import it.unibs.ingsw.destinazioni.domain.dto.VisitInformationDTO;

@RestController
@RequestMapping("/api/visit-plan")
@RequiredArgsConstructor
public class VisitPlanController {
    private final VisitPlanUseCase visitPlanService;

    private final VisitMapper visitMapper;

    @GetMapping("/can-create")
    public ResponseEntity<Boolean> canCreateVisitPlan() {
        return ResponseEntity.ok(visitPlanService.canCreateVisitPlan());
    }


    @PostMapping("/create")
    public ResponseEntity<Void> createVisitPlan() {
        try {
            visitPlanService.createVisitPlan();
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/get-month-visit-plan")
    public ResponseEntity<List<VisitInformationDTO>> getVisitPlan(int month, int year) {
        try {
            List<Visit> visits = visitPlanService.getVisitPlan(month, year);
            List<VisitInformationDTO> visitsDTO = visits.stream()
                    .map(visitMapper::toVisitInformationDTO)
                    .toList();

            return ResponseEntity.ok(visitsDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/next-month")
    public ResponseEntity<Integer> getNextMonth() {
        try {
            int month = visitPlanService.getMonth();
            return ResponseEntity.ok(month);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/next-month-year")
    public ResponseEntity<Integer> getNextMonthYear(){
        try{
            int month = visitPlanService.getMonth();
            if(month == 1) //il prossimo mese é gennaio
                return ResponseEntity.ok( visitPlanService.getYear()+1 );
            else return ResponseEntity.ok( visitPlanService.getYear() );
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }
    }
}
