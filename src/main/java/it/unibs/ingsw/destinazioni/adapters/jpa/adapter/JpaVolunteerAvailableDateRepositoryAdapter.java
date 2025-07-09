package it.unibs.ingsw.destinazioni.adapters.jpa.adapter;

import it.unibs.ingsw.destinazioni.adapters.jpa.entity.VolunteerAvailableDateEntity;
import it.unibs.ingsw.destinazioni.adapters.jpa.entity.VolunteerAvailableDateEntityId;
import it.unibs.ingsw.destinazioni.adapters.jpa.repository.VolunteerAvailableDateRepository;
import it.unibs.ingsw.destinazioni.application.port.out.VolunteerAvailableDateRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.model.VolunteerAvailableDate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class JpaVolunteerAvailableDateRepositoryAdapter implements VolunteerAvailableDateRepositoryPort {

    private final VolunteerAvailableDateRepository repository;

    public JpaVolunteerAvailableDateRepositoryAdapter(VolunteerAvailableDateRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(VolunteerAvailableDate availableDate) {
        VolunteerAvailableDateEntity entity = new VolunteerAvailableDateEntity();
        VolunteerAvailableDateEntityId id = new VolunteerAvailableDateEntityId();
        id.setVolunteerId(availableDate.getVolunteerId());
        id.setAvailableDate(availableDate.getAvailableDate());
        entity.setId(id);
        repository.save(entity);
    }

    @Override
    public void delete(VolunteerAvailableDate availableDate) {
        VolunteerAvailableDateEntityId id = new VolunteerAvailableDateEntityId();
        id.setVolunteerId(availableDate.getVolunteerId());
        id.setAvailableDate(availableDate.getAvailableDate());
        repository.deleteById(id);
    }

    @Override
    public boolean exists(int volunteerId, LocalDate date) {
        VolunteerAvailableDateEntityId id = new VolunteerAvailableDateEntityId();
        id.setVolunteerId(volunteerId);
        id.setAvailableDate(date);
        return repository.existsById(id);
    }

    @Override
    public List<VolunteerAvailableDate> findByVolunteerId(int volunteerId) {
        return repository.findAll()
                .stream()
                .filter(entity -> entity.getId().getVolunteerId() == volunteerId)
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<VolunteerAvailableDate> findByDate(LocalDate date) {
        return repository.findAll()
                .stream()
                .filter(entity -> entity.getId().getAvailableDate().equals(date))
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private VolunteerAvailableDate toDomain(VolunteerAvailableDateEntity entity) {
        return new VolunteerAvailableDate(
                entity.getId().getVolunteerId(),
                entity.getId().getAvailableDate()
        );
    }
}
