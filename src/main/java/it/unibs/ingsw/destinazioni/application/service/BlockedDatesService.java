package it.unibs.ingsw.destinazioni.application.service;



import it.unibs.ingsw.destinazioni.application.port.in.BlockedDatesUseCase;
import it.unibs.ingsw.destinazioni.application.port.out.BlockedDatesRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.model.BlockedDates;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BlockedDatesService implements BlockedDatesUseCase {

    private final BlockedDatesRepositoryPort repository;

    @Override
    public void updateBlockedDates(Set<LocalDate> blockedDates) {
        BlockedDates dates = new BlockedDates(blockedDates);
        repository.updateByMonth(dates, this.getMonthToUpdate());
    }

    @Override
    public BlockedDates getBlockedDates(int month){
        return repository.loadAll();
    }

    private Month getMonthToUpdate(){
       int currentMonth = LocalDate.now().getMonth().getValue();
       return Month.of(currentMonth + 3);
    }
}
