package it.unibs.ingsw.destinazioni.ui.rest.controller;

import it.unibs.ingsw.destinazioni.application.port.in.VolunteerAvailabilityControlUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;




@RestController
@RequestMapping("/api/volunteer-availability")
public class VolunteerAvailabilityController {

    private final VolunteerAvailabilityControlUseCase useCase;

    public VolunteerAvailabilityController(VolunteerAvailabilityControlUseCase useCase) {
        this.useCase = useCase;
    }


    @GetMapping("/status")
    public ResponseEntity<Boolean> getAvailabilityStatus() {
        return ResponseEntity.ok(useCase.canEnableAvailability());
    }


    @PostMapping("/enable")
    public ResponseEntity<Void> enableAvailability() {
        try {
            useCase.enableAvailability();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }


    @PostMapping("/disable")
    public ResponseEntity<Void> disableAvailability() {
        try {
            useCase.disableAvailability();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }


    @GetMapping("/can-enable")
    public ResponseEntity<Boolean> canEnable() {
        return ResponseEntity.ok(useCase.canEnableAvailability());
    }


    @GetMapping("/can-disable")
    public ResponseEntity<Boolean> canDisable() {
        return ResponseEntity.ok(useCase.canDisableAvailability());
    }

    @GetMapping("/month-to-enable")
    public ResponseEntity<Integer> monthToEnable() {
        
        return ResponseEntity.ok(useCase.getMonthToEnable());
    }

    @GetMapping("/month-to-disable")
    public ResponseEntity<Integer> monthToDisable() {
        
        return ResponseEntity.ok(useCase.getMonthToDisable());
    }
    
}
