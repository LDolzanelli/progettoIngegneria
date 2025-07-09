package it.unibs.ingsw.destinazioni.domain.model;

import java.time.LocalDate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class VolunteerAvailableDate {
    private final int volunteerId;
    private final LocalDate availableDate;
}