package it.unibs.ingsw.destinazioni.domain.dto;

import java.util.List;

public record VisitTypeDTO(
        Integer id,
        String title,
        String description,
        String meetingPoint,
        String startDate,
        String endDate,
        String startTime,
        int duration,
        int maxParticipants,
        int minParticipants,
        boolean isFree,
        List<String> daysAvailable,
        List<String> volunteers,
        boolean canBeRemoved,
        boolean canBeModified
) {}