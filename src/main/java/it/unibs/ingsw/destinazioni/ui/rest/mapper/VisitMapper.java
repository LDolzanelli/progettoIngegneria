package it.unibs.ingsw.destinazioni.ui.rest.mapper;

import it.unibs.ingsw.destinazioni.application.port.in.ManageLocationUseCase;
import it.unibs.ingsw.destinazioni.domain.dto.VisitInformationDTO;
import it.unibs.ingsw.destinazioni.domain.dto.VolunteerVisitSummaryDTO;
import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.model.Visit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

@Component
@RequiredArgsConstructor
public class VisitMapper {

    private final ManageLocationUseCase locationService;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public VisitInformationDTO toVisitInformationDTO(Visit visit) {
        var visitType = visit.getVisitType();
        var location = locationService.getLocationForVisitType(visitType);

        HashMap<String, List<String>> visitorsPerBookingCode = new HashMap<>();
        visit.getBookings().forEach(booking ->
                visitorsPerBookingCode.put(booking.getBookingCode(), booking.getVisitorsNames())
        );

        String volunteerNickname = "";
        User volunteer = visit.getVolunteer();
        if (volunteer != null) {
            volunteerNickname = volunteer.getNickname();
        }

        return new VisitInformationDTO(
                visit.getId(),
                volunteerNickname,
                visit.getDate().format(dateFormatter),
                visitType.getStartTime().toString(),
                visitType.getDuration(),
                visit.visitorsNumber(),
                visitType.getMaxParticipants(),
                visitType.getTitle(),
                visitType.getDescription(),
                location.getName(),
                location.getAddress().getFullAddress(),
                visitType.getMeetingPoint(),
                visit.getVisitStatus().getItalianName(),
                visit.getDate().getDayOfWeek().toString(),
                visitorsPerBookingCode,
                visitType.isFree()
        );
    }
}
