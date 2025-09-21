package it.unibs.ingsw.destinazioni.ui.rest.mapper;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Component;

import it.unibs.ingsw.destinazioni.application.port.in.location.LocationQueryUseCase;
import it.unibs.ingsw.destinazioni.domain.dto.VisitInformationDTO;
import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.model.Visit;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class VisitMapper {

    private final LocationQueryUseCase locationQueryService;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public VisitInformationDTO toVisitInformationDTO(Visit visit) {
        var visitType = visit.getVisitType();
        var location = locationQueryService.getLocationForVisitType(visitType);

        HashMap<String, List<String>> visitorsPerBookingCode = new HashMap<>();
        visit.getBookings()
                .forEach(booking -> visitorsPerBookingCode.put(booking.getBookingCode(), booking.getVisitorsNames()));

        String volunteerNickname = "";
        User volunteer = visit.getVolunteer();
        if (volunteer != null) {
            volunteerNickname = volunteer.getNickname();
        }

        return new VisitInformationDTO(visit.getId(), volunteerNickname, visit.getDate().format(dateFormatter),
                visitType.getStartTime().toString(), visitType.getDuration(), visit.visitorsNumber(),
                visitType.getMaxParticipants(), visitType.getTitle(), visitType.getDescription(), location.getName(),
                location.getAddress().getStreet(), location.getAddress().getStreetNumber(),
                location.getAddress().getTown(), location.getAddress().getProvince(), visitType.getMeetingPoint(),
                visit.getVisitStatus().getItalianName(), visit.getDate().getDayOfWeek().toString(),
                visitorsPerBookingCode, visitType.isFree());
    }
}
