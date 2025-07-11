package it.unibs.ingsw.destinazioni.ui.rest.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import it.unibs.ingsw.destinazioni.application.port.in.VisitPlanUseCase;
import it.unibs.ingsw.destinazioni.domain.model.Visit;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/visit-plan")
@RequiredArgsConstructor
public class VisitPlanController {
    private final VisitPlanUseCase useCase;

    @GetMapping("/can-create")
    public ResponseEntity<Boolean> canCreateVisitPlan() {
        return ResponseEntity.ok(useCase.canCreateVisitPlan());
    }


    @PostMapping("/create")
    public ResponseEntity<Void> createVisitPlan() {
        try {
            useCase.createVisitPlan();
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/plan")
    public ResponseEntity<List<Visit>> getVisitPlan(int month, int year) {
        try {
            List<Visit> visits = useCase.getVisitPlan(month, year);
            return ResponseEntity.ok(visits);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/next-month")
    public ResponseEntity<Integer> getNextMonth() {
        try {
            int month = useCase.getMonth();
            return ResponseEntity.ok(month);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
