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
import org.springframework.transaction.annotation.Transactional;

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
    public BlockedDates loadByMonth(int month, int year) {
        Set<LocalDate> dates = repository.findAll()
                .stream()
                .map(BlockedDatesEntity::getDate)
                .filter(date -> month == date.getMonth().getValue() && year == date.getYear() )
                .collect(Collectors.toSet());
        return new BlockedDates(dates);
    }

    @Override
    @Transactional
    public void updateByMonth(BlockedDates dates, int month, int year) {
        BlockedDates toBeRemoved = loadByMonth(month, year);
        toBeRemoved.getDates().forEach(this::delete);
        dates.getDates().forEach(this::save);
    }
}
