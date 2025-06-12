package it.unibs.ingsw.destinazioni.domain.model;

import java.time.LocalDate;
import java.util.Set;
import lombok.Getter;

@Getter
public class BlockedDates {

    private final Set<LocalDate> dates;

    public BlockedDates(Set<LocalDate> dates) {
        this.dates = dates;
    }

    public BlockedDates() {
        this.dates = Set.of();
    }

    public boolean isBlocked(LocalDate date) {
        return dates.contains(date);
    }
}
