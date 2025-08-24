package it.unibs.ingsw.destinazioni.domain.dto;

import java.util.List;

public record BookingDetailsDTO(
    Integer userId,
    String userName,
    List<String> visitorsNames,
    String visitTypeName,
    String locationName,
    String visitDate

) {
    
}
