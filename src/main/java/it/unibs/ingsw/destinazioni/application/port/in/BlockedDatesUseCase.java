package it.unibs.ingsw.destinazioni.application.port.in;

import it.unibs.ingsw.destinazioni.domain.model.BlockedDates;

import java.time.LocalDate;
import java.time.Month;
import java.util.Set;


public interface BlockedDatesUseCase {
    void updateBlockedDates(Set<LocalDate> blockedDates);
    BlockedDates getBlockedDates();
    BlockedDates getBlockedDates(int month);
    Month getMonthToUpdate();
}

