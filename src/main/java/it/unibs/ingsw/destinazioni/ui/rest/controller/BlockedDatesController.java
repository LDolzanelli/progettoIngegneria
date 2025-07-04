package it.unibs.ingsw.destinazioni.ui.rest.controller;

import it.unibs.ingsw.destinazioni.application.port.in.BlockedDatesUseCase;
import it.unibs.ingsw.destinazioni.domain.dto.BlockedDatesDTO;
import it.unibs.ingsw.destinazioni.domain.dto.LocationDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

@RestController
@RequestMapping("/api/blocked-dates")
@RequiredArgsConstructor
public class BlockedDatesController {

    private final BlockedDatesUseCase blockedDatesUseCase;

    @PostMapping("/set")
    public ResponseEntity<Void> addBlockedDates(@RequestBody BlockedDatesDTO blockedDatesDTO, HttpServletRequest request) {
        try{

            if(blockedDatesDTO.equals(null))
                System.out.println("Error");
            else System.out.println("Dates inserted: " + blockedDatesDTO.dateList());

            Set<LocalDate> blockedDates = new HashSet<>();

            for(String date: blockedDatesDTO.dateList())
                try{
                    LocalDate parsedDate = LocalDate.parse(date);
                    blockedDates.add(parsedDate);
                }catch (DateTimeParseException e){
                    throw new IllegalArgumentException("Formato data non valido: " + e.getMessage());
                }

            System.out.println("Date end -->");

            blockedDatesUseCase.updateBlockedDates(blockedDates);

            return ResponseEntity.ok().build();
        }catch (IllegalArgumentException iae){
            System.out.println(iae.getMessage());
            return ResponseEntity.badRequest().build();
        }catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
