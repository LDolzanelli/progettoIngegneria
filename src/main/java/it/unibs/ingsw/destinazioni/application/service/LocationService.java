package it.unibs.ingsw.destinazioni.application.service;

import it.unibs.ingsw.destinazioni.application.port.in.ManageLocationUseCase;
import it.unibs.ingsw.destinazioni.application.port.out.LocationRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.model.Location;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationService implements ManageLocationUseCase {

    private final LocationRepositoryPort repository;

    @Override
    public void addLocation(Location location) {
        repository.save(location);
    }

    @Override
    public void removeLocation(int locationId) {
        repository.deleteById(locationId);
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
}