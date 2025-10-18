package it.unibs.ingsw.destinazioni.application.rest.mapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import it.unibs.ingsw.destinazioni.application.port.in.visittype.VisitTypeValidationUseCase;
import it.unibs.ingsw.destinazioni.domain.dto.VisitTypeDTO;
import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.model.VisitType;
import it.unibs.ingsw.destinazioni.domain.model.enums.DaysOfWeek;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class VisitTypeMapper {

    private final VisitTypeValidationUseCase visitTypeValidationService;

    public VisitTypeDTO toDTO(VisitType visitType) {
        List<String> days =
                visitType.getDaysAvailable() != null ? visitType.getDaysAvailable().stream().map(Enum::name).toList()
                        : Collections.emptyList();

        List<String> volunteers =
                visitType.getVolunteers() != null ? visitType.getVolunteers().stream().map(User::getNickname).toList()
                        : Collections.emptyList();

        return new VisitTypeDTO(visitType.getId(), visitType.getTitle(), visitType.getDescription(),
                visitType.getMeetingPoint(),
                visitType.getStartDate() != null ? visitType.getStartDate().toString() : null,
                visitType.getEndDate() != null ? visitType.getEndDate().toString() : null,
                visitType.getStartTime() != null ? visitType.getStartTime().toString() : null, visitType.getDuration(),
                visitType.getMaxParticipants(), visitType.getMinParticipants(), visitType.isFree(), days, volunteers,
                visitTypeValidationService.canBeRemoved(visitType.getId()),
                visitTypeValidationService.canBeModified(visitType.getId()));
    }


    public VisitType toDomain(VisitTypeDTO dto, List<User> volunteers) {
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
                ? dto.daysAvailable().stream().map(String::toUpperCase).map(DaysOfWeek::valueOf).toList()
                : Collections.emptyList();

        return new VisitType(dto.id(), dto.title(), dto.description(), dto.meetingPoint(), startDate, endDate,
                startTime, dto.duration(), dto.maxParticipants(), dto.minParticipants(), dto.isFree(), days,
                volunteers);
    }
}
