package it.unibs.ingsw.destinazioni.application.rest.controller;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.unibs.ingsw.destinazioni.application.port.in.blockeddates.BlockedDatesUseCase;
import it.unibs.ingsw.destinazioni.domain.dto.BlockedDatesDTO;
import it.unibs.ingsw.destinazioni.domain.model.BlockedDates;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/blocked-dates")
@RequiredArgsConstructor
public class BlockedDatesController {

    private final BlockedDatesUseCase blockedDatesUseCase;

    @GetMapping("/month-to-update")
    public ResponseEntity<Integer> getMonthToUpdate() {
        Month month = blockedDatesUseCase.getMonthToUpdate().getMonth();
        return ResponseEntity.ok(month.getValue());
    }


    @GetMapping("/month-dates")
    public ResponseEntity<Set<String>> getBlockedDatesForMonth() {
        YearMonth month = blockedDatesUseCase.getMonthToUpdate();
        BlockedDates blockedDates = blockedDatesUseCase.getBlockedDates(month.getMonthValue(), month.getYear());

        Set<String> dates = blockedDates.getDates().stream()
                .filter(d -> d.getMonth() == month.getMonth())
                .map(LocalDate::toString).collect(Collectors.toSet());

        return ResponseEntity.ok(dates);
    }


    @GetMapping("/get")
    public ResponseEntity<Set<String>> getBlockedDates() {
        BlockedDates blockedDates = blockedDatesUseCase.getBlockedDates();
        Set<String> dates = blockedDates.getDates().stream()
                .sorted()
                .map(LocalDate::toString)
                .collect(Collectors.toCollection(LinkedHashSet::new)); // ordine cronologico

        return ResponseEntity.ok(dates);
    }


    @PostMapping("/set")
    public ResponseEntity<Void> addBlockedDates(@RequestBody BlockedDatesDTO blockedDatesDTO,
            HttpServletRequest request) {


        Set<LocalDate> blockedDates = new HashSet<>();

        for (String date : blockedDatesDTO.dateList())
            try {
                LocalDate parsedDate = LocalDate.parse(date);
                blockedDates.add(parsedDate);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Formato data non valido: " + e.getMessage());
            }

        blockedDatesUseCase.updateBlockedDates(blockedDates);

        return ResponseEntity.ok().build();

    }
}
