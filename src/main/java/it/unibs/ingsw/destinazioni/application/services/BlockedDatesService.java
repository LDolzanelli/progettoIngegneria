package it.unibs.ingsw.destinazioni.application.services;

import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Set;

import org.springframework.stereotype.Service;

import it.unibs.ingsw.destinazioni.application.port.in.blockeddates.BlockedDatesUseCase;
import it.unibs.ingsw.destinazioni.application.port.out.BlockedDatesRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.model.BlockedDates;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlockedDatesService implements BlockedDatesUseCase {

    private final BlockedDatesRepositoryPort repository;
    private final Clock clock;

    @Override
    public void updateBlockedDates(Set<LocalDate> blockedDates) {
        BlockedDates dates = new BlockedDates(blockedDates);
        repository.updateByMonth(dates, this.getMonthToUpdate().getMonth().getValue(),
                this.getMonthToUpdate().getYear());
    }

    @Override
    public BlockedDates getBlockedDates() {
        return repository.loadAll();
    }

    @Override
    public BlockedDates getBlockedDates(int month, int year) {

        if (month > 12 || month < 1) {
            throw new IllegalArgumentException("Invalid month: " + month);
        }

        return repository.loadByMonth(month, year);
    }

    @Override
    public YearMonth getMonthToUpdate() {
        LocalDate today = LocalDate.now(clock);

        int baseMonth = today.getDayOfMonth() < 16 ? today.getMonthValue() : today.plusMonths(1).getMonthValue();

        // il risultato del modulo può essere 0. si fa +1 alla fine per garantire di
        // avere il mese desiderato
        int targetMonth = ((baseMonth + 2) % 12) + 1;
        return YearMonth.of(baseMonth + 2 > 12 ? today.getYear() + 1 : today.getYear(), targetMonth);
    }

}
