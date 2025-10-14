package it.unibs.ingsw.destinazioni.ui.rest.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.unibs.ingsw.destinazioni.application.port.in.visitplan.CreateVisitPlanUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.visitplan.VisitPlanQueryUseCase;
import it.unibs.ingsw.destinazioni.domain.dto.VisitInformationDTO;
import it.unibs.ingsw.destinazioni.domain.model.Visit;
import it.unibs.ingsw.destinazioni.ui.rest.mapper.VisitMapper;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/visit-plan")
@RequiredArgsConstructor
public class VisitPlanController {

    private final CreateVisitPlanUseCase createVisitPlanService;
    private final VisitPlanQueryUseCase queryVisitPlanService;

    private final VisitMapper visitMapper;

    @GetMapping("/can-create")
    public ResponseEntity<Boolean> canCreateVisitPlan() {
        return ResponseEntity.ok(createVisitPlanService.canCreateVisitPlan());
    }


    @PostMapping("/create")
    public ResponseEntity<Void> createVisitPlan() {

        createVisitPlanService.createVisitPlan();
        return ResponseEntity.ok().build();

    }


    @GetMapping("/get-visit-plan-after-today")
    public ResponseEntity<List<VisitInformationDTO>> getVisitPlan() {

        List<Visit> visits = queryVisitPlanService.getAllVisitsAfterToday();
        List<VisitInformationDTO> visitsDTO = visits.stream().map(visitMapper::toVisitInformationDTO).toList();

        return ResponseEntity.ok(visitsDTO);

    }


    @GetMapping("/get-completed-visits")
    public ResponseEntity<List<VisitInformationDTO>> getCopletedVisitsForArchive() {

        List<Visit> visits = queryVisitPlanService.getAllCompletedVisits();
        List<VisitInformationDTO> visitsDTO = visits.stream().map(visitMapper::toVisitInformationDTO).toList();

        return ResponseEntity.ok(visitsDTO);

    }


    @GetMapping("/next-month")
    public ResponseEntity<Integer> getNextMonth() {

        int month = createVisitPlanService.getMonth();
        return ResponseEntity.ok(month);

    }


    @GetMapping("/next-month-year")
    public ResponseEntity<Integer> getNextMonthYear() {

        int month = createVisitPlanService.getMonth();
        if (month == 1) //il prossimo mese é gennaio
            return ResponseEntity.ok(createVisitPlanService.getYear() + 1);
        else
            return ResponseEntity.ok(createVisitPlanService.getYear());

    }
}
