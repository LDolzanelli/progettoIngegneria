package it.unibs.ingsw.destinazioni.ui.rest.controller;

import it.unibs.ingsw.destinazioni.application.port.in.GetUserInfoUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.VolunteersAvailabilityUseCase;
import it.unibs.ingsw.destinazioni.domain.dto.AvailabilityDatesDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/volunteers-availability")
@RequiredArgsConstructor
public class VolunteerAvailabilityController {

    private final VolunteersAvailabilityUseCase volunteersUseCase;
    private final GetUserInfoUseCase userInfoUseCase;

    @GetMapping("/month-to-update")
    public ResponseEntity<Integer> getMonthToUpdate() {
        Month month = volunteersUseCase.getTargetMonth();
        return ResponseEntity.ok(month.getValue());
    }

    @PutMapping("/update-own")
    public ResponseEntity<Void> updateOwnAvailability(@AuthenticationPrincipal UserDetails user,
                                                      @RequestBody AvailabilityDatesDTO dto) {
        int volunteerId = userInfoUseCase.getIdByNickname(user.getUsername());

        //conversione da String a LocalDate
        Set<LocalDate> dates = dto.dateList().stream()
                .map(LocalDate::parse)
                .collect(Collectors.toSet());

        volunteersUseCase.updateAvailability(volunteerId, dates);
        volunteersUseCase.updateAvailability(volunteerId, dates);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get-own")
    public ResponseEntity<Set<LocalDate>> getOwnAvailability(@AuthenticationPrincipal UserDetails user,
                                                             @RequestParam(required = false) Integer month) {
        int volunteerId = userInfoUseCase.getIdByNickname(user.getUsername());
        Set<LocalDate> dates = month == null
                ? volunteersUseCase.getAvailability(volunteerId)
                : volunteersUseCase.getAvailability(volunteerId, Month.of(month));
        return ResponseEntity.ok(dates);
    }

    @GetMapping("/get/{volunteerId}")
    public ResponseEntity<Set<LocalDate>> getAvailabilityByVolunteerId(@PathVariable int volunteerId,
                                                                       @RequestParam(required = false) Integer month) {
        Set<LocalDate> dates = month == null
                ? volunteersUseCase.getAvailability(volunteerId)
                : volunteersUseCase.getAvailability(volunteerId, Month.of(month));
        return ResponseEntity.ok(dates);
    }
}
