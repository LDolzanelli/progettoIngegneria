package it.unibs.ingsw.destinazioni.domain.dto;

import java.util.List;

public record VisitTypeDTO(
        Integer id,
        String title,
        String description,
        String meetingPoint,
        String startDate,      // ISO date e.g. "2025-05-22"
        String endDate,        // ISO date
        String startTime,      // ISO time e.g. "14:30:00"
        int duration,
        int maxParticipants,
        int minParticipants,
        boolean isFree,
        List<String> daysAvailable,  // list of day names as Strings, e.g. ["Monday", "Wednesday"]
        List<String> volunteers,
        boolean canBeRemoved 
) {}