package it.unibs.ingsw.destinazioni.ui.rest.controller;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.unibs.ingsw.destinazioni.application.port.in.user.GetUserInfoUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.visittype.AssignVolunteerToVisitTypeUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.visittype.VisitTypeCommandUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.visittype.VisitTypeQueryUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.visittype.VisitTypeValidationUseCase;
import it.unibs.ingsw.destinazioni.domain.dto.VisitTypeDTO;
import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.model.VisitType;
import it.unibs.ingsw.destinazioni.ui.rest.mapper.VisitTypeMapper;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/visit-type")
@RequiredArgsConstructor
public class VisitTypeController {

    private final VisitTypeQueryUseCase VisitTypeQueryService;
    private final VisitTypeCommandUseCase VisitTypeCommandService;
    private final VisitTypeValidationUseCase visitTypeValidationService;
    private final AssignVolunteerToVisitTypeUseCase assignVolunteerToVisitTypeService;
    private final GetUserInfoUseCase getUserInfoUseCase;
    private final VisitTypeMapper mapper;

    @PostMapping("/add/{locationId}")
    public ResponseEntity<String> addVisitType(@RequestBody VisitTypeDTO dto, @PathVariable int locationId) {

        List<User> resolvedVolunteers = getUserInfoUseCase.findAllByNicknames(dto.volunteers());
        var visitType = mapper.toDomain(dto, resolvedVolunteers);
        VisitTypeCommandService.addVisitType(visitType, locationId);
        return ResponseEntity.ok().build();

    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteVisitType(@PathVariable int id) {

        VisitTypeCommandService.removeVisitType(id);
        return ResponseEntity.ok().build();

    }


    @GetMapping("/list/{locationId}")
    public ResponseEntity<Set<VisitTypeDTO>> listByLocation(@PathVariable int locationId) {
        Set<VisitType> visitTypes = VisitTypeQueryService.listByLocation(locationId);
        Set<VisitTypeDTO> dtos = visitTypes.stream().map(mapper::toDTO).collect(Collectors.toSet());
        return ResponseEntity.ok(dtos);
    }


    @GetMapping("/{id}")
    public ResponseEntity<VisitTypeDTO> getVisitType(@PathVariable int id) {
        Optional<VisitType> visitTypeOpt = VisitTypeQueryService.findById(id);
        return visitTypeOpt.map(v -> ResponseEntity.ok(mapper.toDTO(v)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PutMapping("/update/{locationId}")
    public ResponseEntity<Void> updateVisitType(@RequestBody VisitTypeDTO dto, @PathVariable int locationId) {

        List<User> resolvedVolunteers = getUserInfoUseCase.findAllByNicknames(dto.volunteers());
        var visitType = mapper.toDomain(dto, resolvedVolunteers);
        VisitTypeCommandService.updateVisitType(visitType);
        return ResponseEntity.ok().build();

    }


    @GetMapping("/volunteer/{volunteerId}")
    public ResponseEntity<Set<VisitTypeDTO>> listByVolunteer(@PathVariable int volunteerId) {

        Set<VisitType> visitTypes = VisitTypeQueryService.listByVolunteerId(volunteerId);
        Set<VisitTypeDTO> dtos = visitTypes.stream().map(mapper::toDTO).collect(Collectors.toSet());
        return ResponseEntity.ok(dtos);

    }


    @PostMapping("/add-volunteer/{visitTypeId}/{nickname}")
    public ResponseEntity<String> addVolunteerToVisitType(@PathVariable int visitTypeId,
            @PathVariable String nickname) {

        assignVolunteerToVisitTypeService.addVolunteerToVisitType(visitTypeId, nickname);
        return ResponseEntity.ok().build();

    }


    @GetMapping("/modification-state-active")
    public ResponseEntity<Boolean> canAddOrRemoveEntities() {
        return ResponseEntity.ok(visitTypeValidationService.isAddOrRemovalStateActive());
    }
}
