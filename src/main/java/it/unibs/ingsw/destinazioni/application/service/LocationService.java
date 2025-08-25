package it.unibs.ingsw.destinazioni.application.service;

import it.unibs.ingsw.destinazioni.application.port.in.ManageLocationUseCase;
import it.unibs.ingsw.destinazioni.application.port.out.LocationRepositoryPort;
import it.unibs.ingsw.destinazioni.application.port.in.ManageVisitTypeUseCase;
import it.unibs.ingsw.destinazioni.domain.model.Location;
import it.unibs.ingsw.destinazioni.domain.model.VisitType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationService implements ManageLocationUseCase {

    private final LocationRepositoryPort repository;
    private final ManageVisitTypeUseCase visitTypeService;

    @Override
    public void addLocation(Location location) {
        repository.save(location);
    }


    @Override
    public void removeLocation(int locationId) {

        Location location = repository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Location con id " + locationId + " non trovata"));

        if (!canBeRemoved(locationId)) {
            throw new IllegalArgumentException("La location non può essere rimossa.");
        }



        if (location.getVisitTypes().isEmpty()) {
            repository.deleteById(location.getId());
        } else {
            // Rimuovo i tipi di visita associati alla location per eliminare anche eventuali volontari
            // associati. La location verrà eliminata dopo la rimozione dei tipi di visita
            location.getVisitTypes().forEach(visitType -> visitTypeService.removeVisitType(visitType.getId()));
        }
    }


    @Override
    public List<Location> listAll() {
        return repository.findAll();
    }


    @Override
    public Optional<Location> findById(int id) {
        return repository.findById(id);
    }


    @Override
    public void updateLocation(Location location) {
        if (location == null || location.getId() == null) {
            throw new IllegalArgumentException("Location o ID non valido");
        }

        var existing = repository.findById(location.getId());
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Location con id " + location.getId() + " non trovata");
        }

        repository.save(location);
    }


    @Override
    public boolean canBeRemoved(int locationId) {

        Location location = repository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Location con id " + locationId + " non trovata"));

        if (location.getVisitTypes() == null || location.getVisitTypes().isEmpty()) {
            return true;
        }

        return location.getVisitTypes().stream()
                .allMatch(visitType -> visitTypeService.canBeRemoved(visitType.getId()));


    }


    @Override
    public Location getLocationForVisitType(VisitType visitType) {
        return repository.findByVisitType(visitType).orElseThrow(() -> new IllegalArgumentException(
                "Nessuna location associata al tipo di visita con id: " + visitType.getId()));
    }
}
