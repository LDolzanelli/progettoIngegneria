package it.unibs.ingsw.destinazioni.ui.rest.controller;

import it.unibs.ingsw.destinazioni.application.port.in.VolunteerAvailabilityControlUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import it.unibs.ingsw.destinazioni.application.port.in.VolunteersAvailabilityUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.GetUserInfoUseCase;
import it.unibs.ingsw.destinazioni.domain.dto.AvailabilityDatesDTO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;








@RestController
@RequestMapping("/api/volunteer-availability")
@RequiredArgsConstructor
public class VolunteerAvailabilityController {

    private final VolunteerAvailabilityControlUseCase useCase;
    private final VolunteersAvailabilityUseCase volunteersUseCase;
    private final GetUserInfoUseCase userInfoUseCase;

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


    @GetMapping("/month-to-update")
    public ResponseEntity<Integer> getMonthToUpdate() {
        Month month = volunteersUseCase.getTargetMonth();
        return ResponseEntity.ok(month.getValue());
    }


    @PutMapping("/update-own")
    public ResponseEntity<Void> updateOwnAvailability(@AuthenticationPrincipal UserDetails user,
                                                      @RequestBody AvailabilityDatesDTO dto) {

        int volunteerId = userInfoUseCase.getIdByNickname(user.getUsername());

        Set<LocalDate> dates = Optional.ofNullable(dto.dateList())  //se null da Optional.empty()
                .orElse(List.of())
                .stream()
                .map(LocalDate::parse)
                .collect(Collectors.toSet());

        volunteersUseCase.updateAvailability(volunteerId, dates);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get-own")
    public ResponseEntity<Set<LocalDate>> getOwnAvailability(@AuthenticationPrincipal UserDetails user,
            @RequestParam(required = false) Integer month) {
        int volunteerId = userInfoUseCase.getIdByNickname(user.getUsername());
        Set<LocalDate> dates = month == null ? volunteersUseCase.getAvailability(volunteerId)
                : volunteersUseCase.getAvailability(volunteerId, Month.of(month));
        return ResponseEntity.ok(dates);
    }


    @GetMapping("/get/{volunteerId}")
    public ResponseEntity<Set<LocalDate>> getAvailabilityByVolunteerId(@PathVariable int volunteerId,
            @RequestParam(required = false) Integer month) {
        Set<LocalDate> dates = month == null ? volunteersUseCase.getAvailability(volunteerId)
                : volunteersUseCase.getAvailability(volunteerId, Month.of(month));
        return ResponseEntity.ok(dates);
    }

}