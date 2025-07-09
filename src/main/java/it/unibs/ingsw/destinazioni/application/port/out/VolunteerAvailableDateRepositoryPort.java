package it.unibs.ingsw.destinazioni.application.port.out;

import it.unibs.ingsw.destinazioni.domain.model.VolunteerAvailableDate;

import java.time.LocalDate;
import java.util.List;

public interface VolunteerAvailableDateRepositoryPort {
    void save(VolunteerAvailableDate availableDate);
    void delete(VolunteerAvailableDate availableDate);
    boolean exists(int volunteerId, LocalDate date);
    List<VolunteerAvailableDate> findByVolunteerId(int volunteerId);
    List<VolunteerAvailableDate> findByDate(LocalDate date);
}
