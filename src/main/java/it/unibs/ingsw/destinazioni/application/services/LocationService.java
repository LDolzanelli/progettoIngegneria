package it.unibs.ingsw.destinazioni.application.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import it.unibs.ingsw.destinazioni.application.port.in.ManageLocationUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.ManageVisitTypeUseCase;
import it.unibs.ingsw.destinazioni.application.port.out.LocationRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.model.Location;
import it.unibs.ingsw.destinazioni.domain.model.VisitType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocationService implements ManageLocationUseCase {

    private final LocationRepositoryPort repository;
    private final ManageVisitTypeUseCase visitTypeService;

    @Override
    /*@ also
      @ requires repository != null;
      @ ensures repository.findById(location.getId()) != null ==> repository.findById(location.getId()).isPresent();
      @*/
    public void addLocation(Location location) {
        repository.save(location);
    }


    @Override
    /*@ also
      @ ensures !repository.findById(locationId).isPresent() ||
      @         (repository.findById(locationId).get().getVisitTypes() != null &&
      @          repository.findById(locationId).get().getVisitTypes().isEmpty());
      @*/
    public void removeLocation(int locationId) {

        Location location = repository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Location con id " + locationId + " non trovata"));

        if (!canBeRemoved(locationId)) {
            throw new IllegalArgumentException("La location non può essere rimossa.");
        }

        if (location.getVisitTypes().isEmpty()) {
            repository.deleteById(location.getId());
        } else {
            // Rimuovo i tipi di visita associati alla location per eliminare anche
            // eventuali volontari
            // associati. La location verrà eliminata dopo la rimozione dei tipi di visita
            location.getVisitTypes().forEach(visitType -> visitTypeService.removeVisitType(visitType.getId()));
        }
    }


    @Override
    /*@ also
      @ ensures \result.equals(repository.findAll());
      @*/
    public List<Location> listAll() {
        return repository.findAll();
    }


    @Override
    /*@ also
      @ ensures \result.equals(repository.findById(id));
      @*/
    public Optional<Location> findById(int id) {
        return repository.findById(id);
    }


    @Override
    /*@ also
      @ ensures repository.findById(location.getId()).isPresent();
      @ ensures repository.findById(location.getId()).get().getName().equals(location.getName());
      @ ensures repository.findById(location.getId()).get().getDescription().equals(location.getDescription());
      @*/
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
    /*@ also
      @ ensures \result == (repository.findById(locationId).get().getVisitTypes() == null ||
      @                    repository.findById(locationId).get().getVisitTypes().isEmpty() ||
      @                    (\forall VisitType vt; repository.findById(locationId).get().getVisitTypes().contains(vt);
      @                     visitTypeService.canBeRemoved(vt.getId())));
      @*/
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
    /*@ also
      @ ensures \result.equals(repository.findByVisitType(visitType).orElseThrow(() -> 
      @         new IllegalArgumentException("Nessuna location associata al tipo di visita con id: " + visitType.getId())));
      @*/
    public Location getLocationForVisitType(VisitType visitType) {
        return repository.findByVisitType(visitType).orElseThrow(() -> new IllegalArgumentException(
                "Nessuna location associata al tipo di visita con id: " + visitType.getId()));
    }
}
