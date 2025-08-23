package it.unibs.ingsw.destinazioni.domain.dto;

public record VolunteerVisitSummaryDTO(
    Integer id,
    String date,
    String startTime,
    String visitTypeTitle,
    String locationName,
    String status,
    int numberOfParticipants
) {}

