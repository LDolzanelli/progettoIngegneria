package it.unibs.ingsw.destinazioni.adapters.jpa.adapter;

import java.time.LocalDate;
import java.time.Month;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import it.unibs.ingsw.destinazioni.adapters.jpa.entity.BlockedDatesEntity;
import it.unibs.ingsw.destinazioni.adapters.jpa.repository.BlockedDatesRepository;
import it.unibs.ingsw.destinazioni.application.port.out.BlockedDatesRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.model.BlockedDates;

@Repository
public class JpaBlockedDatesRepositoryAdapter implements BlockedDatesRepositoryPort {

    private final BlockedDatesRepository repository;

    public JpaBlockedDatesRepositoryAdapter(BlockedDatesRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(LocalDate date) {
        BlockedDatesEntity entity = new BlockedDatesEntity();
        entity.setDate(date);
        repository.save(entity);
    }

    @Override
    public void delete(LocalDate date) {
        repository.deleteByDate(date);
    }

    @Override
    public boolean isBlocked(LocalDate date) {
        return repository.existsByDate(date);
    }

    @Override
    public BlockedDates loadAll() {
        Set<LocalDate> dates = repository.findAll()
                .stream()
                .map(BlockedDatesEntity::getDate)
                .collect(Collectors.toSet());
        return new BlockedDates(dates);
    }

    @Override
    public BlockedDates loadByMonth(Month month) {
        Set<LocalDate> dates = repository.findAll()
                .stream()
                .map(BlockedDatesEntity::getDate)
                .filter(date -> month.equals(date.getMonth()))
                .collect(Collectors.toSet());
        return new BlockedDates(dates);
    }

    @Override
    public void updateByMonth(BlockedDates dates, Month month) {
        BlockedDates toBeRemoved = loadByMonth(month);
        toBeRemoved.getDates().forEach(this::delete);
        dates.getDates().forEach(this::save);
    }
}
