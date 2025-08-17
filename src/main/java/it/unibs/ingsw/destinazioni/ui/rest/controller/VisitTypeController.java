package it.unibs.ingsw.destinazioni.ui.rest.controller;

import it.unibs.ingsw.destinazioni.application.port.in.GetUserInfoUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.ManageVisitTypeUseCase;
import it.unibs.ingsw.destinazioni.domain.dto.VisitTypeDTO;
import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.model.VisitType;
import it.unibs.ingsw.destinazioni.domain.model.enums.DaysOfWeek;
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

    @PostMapping("/add/{locationId}")
    public ResponseEntity<String> addVisitType(@RequestBody VisitTypeDTO dto, @PathVariable int locationId) {
        try {
            System.out.println("DTO Ricevuto: " + dto.toString());
            List<User> resolvedVolunteers = getUserInfoUseCase.findAllByNicknames(dto.volunteers());
            for(User volunteer : resolvedVolunteers) {
                System.out.println(volunteer.getId());
            }
            var visitType = mapToDomain(dto, resolvedVolunteers);
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
        Set<VisitTypeDTO> dtos = visitTypes.stream().map(this::mapToDTO).collect(Collectors.toSet());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VisitTypeDTO> getVisitType(@PathVariable int id) {
        Optional<VisitType> visitTypeOpt = manageVisitTypeUseCase.findById(id);
        return visitTypeOpt.map(v -> ResponseEntity.ok(mapToDTO(v)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/update/{locationId}")
    public ResponseEntity<Void> updateVisitType(@RequestBody VisitTypeDTO dto, @PathVariable int locationId) {
        try {
            List<User> resolvedVolunteers = getUserInfoUseCase.findAllByNicknames(dto.volunteers());
            var visitType = mapToDomain(dto, resolvedVolunteers);
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
                    .map(this::mapToDTO)
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

    // ======= mappers ==========

    private VisitTypeDTO mapToDTO(VisitType visitType) {
        List<String> days = visitType.getDaysAvailable() != null
                ? visitType.getDaysAvailable().stream().map(Enum::name).toList()
                : Collections.emptyList();

        List<String> volunteers = visitType.getVolunteers() != null
                ? visitType.getVolunteers().stream().map(User::getNickname).toList()
                : Collections.emptyList();

        return new VisitTypeDTO(
                visitType.getId(),
                visitType.getTitle(),
                visitType.getDescription(),
                visitType.getMeetingPoint(),
                visitType.getStartDate() != null ? visitType.getStartDate().toString() : null,
                visitType.getEndDate() != null ? visitType.getEndDate().toString() : null,
                visitType.getStartTime() != null ? visitType.getStartTime().toString() : null,
                visitType.getDuration(),
                visitType.getMaxParticipants(),
                visitType.getMinParticipants(),
                visitType.isFree(),
                days,
                volunteers,
                manageVisitTypeUseCase.canBeRemoved(visitType.getId()),
                manageVisitTypeUseCase.canBeModified(visitType.getId())
        );
    }

    private VisitType mapToDomain(VisitTypeDTO dto, List<User> volunteers) {
        LocalDate startDate = null;
        LocalDate endDate = null;
        LocalTime startTime = null;

        try {
            if (dto.startDate() != null && !dto.startDate().isBlank())
                startDate = LocalDate.parse(dto.startDate());
            if (dto.endDate() != null && !dto.endDate().isBlank())
                endDate = LocalDate.parse(dto.endDate());
            if (dto.startTime() != null && !dto.startTime().isBlank())
                startTime = LocalTime.parse(dto.startTime());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato data/ora non valido: " + e.getMessage());
        }

        List<DaysOfWeek> days = dto.daysAvailable() != null
                ? dto.daysAvailable().stream()
                .map(String::toUpperCase)
                .map(DaysOfWeek::valueOf)
                .toList()
                : Collections.emptyList();

        return new VisitType(
                dto.id(),
                dto.title(),
                dto.description(),
                dto.meetingPoint(),
                startDate,
                endDate,
                startTime,
                dto.duration(),
                dto.maxParticipants(),
                dto.minParticipants(),
                dto.isFree(),
                days,
                volunteers
        );
    }
}
