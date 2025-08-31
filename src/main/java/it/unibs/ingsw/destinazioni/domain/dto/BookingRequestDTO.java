package it.unibs.ingsw.destinazioni.domain.dto;

import java.util.List;

public record BookingRequestDTO(Integer visitId, Integer userId, List<String> visitorsNames) {
}
