package it.unibs.ingsw.destinazioni.application.service;

import it.unibs.ingsw.destinazioni.application.port.in.ManageVisitTypeUseCase;
import it.unibs.ingsw.destinazioni.application.port.out.UserRepositoryPort;
import it.unibs.ingsw.destinazioni.application.port.out.VisitTypeRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.model.VisitType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class VisitTypeService implements ManageVisitTypeUseCase {

    private final VisitTypeRepositoryPort repository;
    private final UserRepositoryPort userRepository;


    @Override
    public void addVisitType(VisitType visitType, int locationId) {
        repository.save(visitType, locationId);
    }

    @Override
    public void removeVisitType(int visitTypeId) {
        repository.deleteById(visitTypeId);
    }

    @Override
    public Set<VisitType> listAll() {
        return repository.findAll();
    }

    public Set<VisitType> listByLocation(int locationId) {
        return repository.findByLocationId(locationId);
    }

    @Override
    public Optional<VisitType> findById(int id) {
        return repository.findById(id);
    }

    @Override
    public void updateVisitType(VisitType visitType, int locationId) {
        if (visitType == null || visitType.getId() == null) {
            throw new IllegalArgumentException("VisitType o ID non valido");
        }

        var existing = repository.findById(visitType.getId());
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Visit Type con id " + visitType.getId() + " non trovata");
        }

        repository.save(visitType, locationId);
    }
}
