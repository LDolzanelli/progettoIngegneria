package it.unibs.ingsw.destinazioni.application.port.in;

import it.unibs.ingsw.destinazioni.domain.model.Location;

import java.util.List;

import java.util.Optional;

import it.unibs.ingsw.destinazioni.domain.model.VisitType;

public interface ManageLocationUseCase {
    void addLocation(Location location);
    void removeLocation(int locationId);
    boolean canBeRemoved(int locationId);
    List<Location> listAll();
    Optional<Location> findById(int id);
    Location getLocationForVisitType(VisitType visitType);
    void updateLocation(Location location);
}