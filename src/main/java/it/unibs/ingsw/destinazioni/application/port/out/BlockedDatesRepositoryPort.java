package it.unibs.ingsw.destinazioni.application.port.out;

import it.unibs.ingsw.destinazioni.domain.model.BlockedDates;

import java.time.LocalDate;
import java.time.Month;

public interface BlockedDatesRepositoryPort {
    void save(LocalDate date);
    void delete(LocalDate date);
    boolean isBlocked(LocalDate date);
    BlockedDates loadAll();
    BlockedDates loadByMonth(int month, int year);
    void updateByMonth(BlockedDates blockedDates, int month, int year);
}
