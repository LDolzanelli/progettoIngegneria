package it.unibs.ingsw.destinazioni.ui.rest.controller;

import it.unibs.ingsw.destinazioni.application.port.in.ManageLocationUseCase;
import it.unibs.ingsw.destinazioni.domain.dto.LocationAddressDTO;
import it.unibs.ingsw.destinazioni.domain.dto.LocationDTO;
import it.unibs.ingsw.destinazioni.domain.model.Location;
import it.unibs.ingsw.destinazioni.domain.model.LocationAddress;
import it.unibs.ingsw.destinazioni.domain.model.VisitType;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/location")
@RequiredArgsConstructor
public class LocationController {

    private final ManageLocationUseCase manageLocationUseCase;

    @PostMapping("/add")
    public ResponseEntity<Void> addLocation(@RequestBody LocationDTO locationDTO) {
        try {
            var location = mapToDomain(locationDTO);
            manageLocationUseCase.addLocation(location);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable int id) {
        try {
            manageLocationUseCase.removeLocation(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<LocationDTO>> listLocations() {
        var locations = manageLocationUseCase.listAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
        return ResponseEntity.ok(locations);
    }

    // Mapper domain a DTO
    private LocationDTO mapToDTO(Location location) {
        var address = location.getAddress();
        return new LocationDTO(
                location.getId(),
                location.getName(),
                location.getDescription(),
                new LocationAddressDTO(
                        address.getStreet(),
                        address.getStreetNumber(),
                        address.getTown(),
                        address.getProvince()
                ),
                null, // puoi mappare visitTypes se vuoi
                manageLocationUseCase.canBeRemoved(location.getId())
        );
    }

    // Mapper DTO a domain
    private Location mapToDomain(LocationDTO dto) {
        var addressDto = dto.address();

        var address = new LocationAddress(
                addressDto.street(),
                addressDto.streetNumber(),
                addressDto.town(),
                addressDto.province()
        );

        List<VisitType> visitTypes;
        if (dto.visitTypes() == null) {
            visitTypes = Collections.emptyList();
        } else {
            visitTypes = dto.visitTypes().stream().map(vdto -> {
                LocalDate startDate = null;
                LocalDate endDate = null;
                LocalTime startTime = null;

                try {
                    if (vdto.startDate() != null && !vdto.startDate().isBlank()) {
                        startDate = LocalDate.parse(vdto.startDate());
                    }
                    if (vdto.endDate() != null && !vdto.endDate().isBlank()) {
                        endDate = LocalDate.parse(vdto.endDate());
                    }
                    if (vdto.startTime() != null && !vdto.startTime().isBlank()) {
                        startTime = LocalTime.parse(vdto.startTime());
                    }
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException("Formato data/ora non valido in VisitType: " + e.getMessage());
                }

                return new VisitType(
                        vdto.id(),
                        vdto.title(),
                        vdto.description(),
                        vdto.meetingPoint(),
                        startDate,
                        endDate,
                        startTime,
                        vdto.duration(),
                        vdto.maxParticipants(),
                        vdto.minParticipants(),
                        vdto.isFree(),
                        Collections.emptyList(), //TODO: implementare giorni
                        Collections.emptyList()  //TODO: implementare volontari
                );
            }).toList();
        }

        return new Location(
                dto.id(),
                dto.name(),
                dto.description(),
                address,
                visitTypes
        );
    }
}
