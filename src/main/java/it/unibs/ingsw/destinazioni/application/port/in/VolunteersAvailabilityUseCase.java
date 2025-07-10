package it.unibs.ingsw.destinazioni.application.port.in;

import java.time.LocalDate;
import java.time.Month;
import java.util.Set;

public interface VolunteersAvailabilityUseCase {
    void updateAvailability(int volunteerId, Set<LocalDate> availableDates);
    Set<LocalDate> getAvailability(int volunteerId);
    Set<LocalDate> getAvailability(int volunteerId, Month month);
    Month getTargetMonth();
}
