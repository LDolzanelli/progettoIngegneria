package it.unibs.ingsw.destinazioni.application.service;



import it.unibs.ingsw.destinazioni.application.port.in.BlockedDatesUseCase;
import it.unibs.ingsw.destinazioni.application.port.out.BlockedDatesRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.model.BlockedDates;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BlockedDatesService implements BlockedDatesUseCase {

    private final BlockedDatesRepositoryPort repository;
    private final Clock clock;

    @Override
    public void updateBlockedDates(Set<LocalDate> blockedDates) {
        BlockedDates dates = new BlockedDates(blockedDates);
        repository.updateByMonth(dates, this.getMonthToUpdate());
    }

    @Override
    public BlockedDates getBlockedDates() {
        return repository.loadAll();
    }

    @Override
    public BlockedDates getBlockedDates(int month) {
        return repository.loadAll();
    }

    @Override
    public Month getMonthToUpdate(){
        LocalDate today = LocalDate.now(clock);
        int baseMonth;

        if (today.getDayOfMonth() < 16) {
            baseMonth = today.getMonthValue();
        } else {
            baseMonth = today.plusMonths(1).getMonthValue();
        }

        //il risultato del modulo può essere 0. si fa +1 alla fine per garantire di avere il mese desiderato
        int targetMonth = ((baseMonth + 2) % 12) + 1;
        return Month.of(targetMonth);
    }
}
