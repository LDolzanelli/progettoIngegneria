package it.unibs.ingsw.destinazioni.ui.rest.controller;

import it.unibs.ingsw.destinazioni.application.port.in.GetUserInfoUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.ManageVisitTypeUseCase;
import it.unibs.ingsw.destinazioni.domain.dto.VisitTypeDTO;
import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.model.VisitType;
import it.unibs.ingsw.destinazioni.domain.model.enums.DaysOfWeek;
import it.unibs.ingsw.destinazioni.ui.rest.mapper.VisitTypeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/visit-type")
@RequiredArgsConstructor
public class VisitTypeController {

    private final ManageVisitTypeUseCase manageVisitTypeUseCase;
    private final GetUserInfoUseCase getUserInfoUseCase;
    private final VisitTypeMapper mapper;

    @PostMapping("/add/{locationId}")
    public ResponseEntity<String> addVisitType(@RequestBody VisitTypeDTO dto, @PathVariable int locationId) {
        try {
            List<User> resolvedVolunteers = getUserInfoUseCase.findAllByNicknames(dto.volunteers());
            var visitType = mapper.toDomain(dto, resolvedVolunteers);
            manageVisitTypeUseCase.addVisitType(visitType, locationId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Dati non validi: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore interno: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteVisitType(@PathVariable int id) {
        try {
            manageVisitTypeUseCase.removeVisitType(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore durante la rimozione: " + e.getMessage());
        }
    }

    @GetMapping("/list/{locationId}")
    public ResponseEntity<Set<VisitTypeDTO>> listByLocation(@PathVariable int locationId) {
        Set<VisitType> visitTypes = manageVisitTypeUseCase.listByLocation(locationId);
        Set<VisitTypeDTO> dtos = visitTypes.stream().map(mapper::toDTO).collect(Collectors.toSet());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VisitTypeDTO> getVisitType(@PathVariable int id) {
        Optional<VisitType> visitTypeOpt = manageVisitTypeUseCase.findById(id);
        return visitTypeOpt.map(v -> ResponseEntity.ok(mapper.toDTO(v)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/update/{locationId}")
    public ResponseEntity<Void> updateVisitType(@RequestBody VisitTypeDTO dto, @PathVariable int locationId) {
        try {
            List<User> resolvedVolunteers = getUserInfoUseCase.findAllByNicknames(dto.volunteers());
            var visitType = mapper.toDomain(dto, resolvedVolunteers);
            manageVisitTypeUseCase.updateVisitType(visitType);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/volunteer/{volunteerId}")
    public ResponseEntity<Set<VisitTypeDTO>> listByVolunteer(@PathVariable int volunteerId) {
        try {
            Set<VisitType> visitTypes = manageVisitTypeUseCase.listByVolunteerId(volunteerId);
            Set<VisitTypeDTO> dtos = visitTypes.stream()
                    .map(mapper::toDTO)
                    .collect(Collectors.toSet());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/add-volunteer/{visitTypeId}/{nickname}")
    public ResponseEntity<String> addVolunteerToVisitType(@PathVariable int visitTypeId, @PathVariable String nickname) {
        try {
            manageVisitTypeUseCase.addVolunteerToVisitType(visitTypeId, nickname);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Dati non validi: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore interno: " + e.getMessage());
        }
    }

    @GetMapping("/modification-state-active")
    public ResponseEntity<Boolean> canAddOrRemoveEntities() {
        return ResponseEntity.ok(manageVisitTypeUseCase.isAddOrRemovalStateActive());
    }
}
