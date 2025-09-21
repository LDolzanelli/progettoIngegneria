package it.unibs.ingsw.destinazioni.domain.dto;

import java.util.List;
import java.util.Map;

public record VisitInformationDTO(Integer id, String volunteerNickname, String date, String startTime, int duration,
        int numberOfParticipants, int maxParticipants, String visitTypeTitle, String visitTypeDescription,
        String locationName, String street, String streetNumber, String town, String province, String meetingPoint,
        String status, String dayOfWeek, Map<String, List<String>> visitorsPerBookingCode, boolean isFree) {
}


