package it.unibs.ingsw.destinazioni.application.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import it.unibs.ingsw.destinazioni.application.exceptions.codes.LocationErrorCode;
import it.unibs.ingsw.destinazioni.application.exceptions.usecases.LocationException;
import it.unibs.ingsw.destinazioni.application.port.in.location.LocationCommandUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.location.LocationQueryUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.location.LocationValidationUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.visittype.VisitTypeCommandUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.visittype.VisitTypeValidationUseCase;
import it.unibs.ingsw.destinazioni.application.port.out.LocationRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.model.Location;
import it.unibs.ingsw.destinazioni.domain.model.VisitType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocationService implements LocationQueryUseCase, LocationCommandUseCase, LocationValidationUseCase {

  private final LocationRepositoryPort repository;
  private final VisitTypeCommandUseCase visitTypeCommands;
  private final VisitTypeValidationUseCase visitTypeValidation;

  @Override
  public void addLocation(Location location) {

    for (Location loc : repository.findAll()) {
      if (loc.getName().equalsIgnoreCase(location.getName())) {
        throw new LocationException(LocationErrorCode.LOCATION_ALREADY_EXISTS,
            "Esiste già una località con questo nome: " + location.getName());
      }
    }
    repository.save(location);
  }


  @Override
  public void removeLocation(int locationId) {

    Location location = repository.findById(locationId)
        .orElseThrow(() -> new LocationException(LocationErrorCode.LOCATION_NOT_FOUND,
            "La località con id " + locationId + " non esiste."));

    if (!canBeRemoved(locationId)) {
      throw new LocationException(LocationErrorCode.CANT_BE_DELETED,
          "La località con id " + locationId + " non può essere eliminata.");
    }

    if (location.getVisitTypes().isEmpty()) {
      repository.deleteById(location.getId());
    } else {
      // Rimuovo i tipi di visita associati alla location per eliminare anche
      // eventuali volontari associati. La location verrà eliminata dopo la rimozione dei tipi di visita
      location.getVisitTypes().forEach(visitType -> visitTypeCommands.removeVisitType(visitType.getId()));
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
      throw new LocationException(LocationErrorCode.NULL_LOCATION, "Location o ID della location null");
    }

    if (repository.findById(location.getId()).isEmpty()) {
      throw new LocationException(LocationErrorCode.LOCATION_NOT_FOUND,
          "La località con id " + location.getId() + " non trovata");
    }

    repository.save(location);
  }


  @Override
  public boolean canBeRemoved(int locationId) {

    Location location = repository.findById(locationId)
        .orElseThrow(() -> new LocationException(LocationErrorCode.LOCATION_NOT_FOUND,
            "La località con id " + locationId + " non esiste."));

    if (location.getVisitTypes() == null || location.getVisitTypes().isEmpty()) {
      return true;
    }

    return location.getVisitTypes().stream().allMatch(visitType -> visitTypeValidation.canBeRemoved(visitType.getId()));

  }


  @Override
  public Location getLocationForVisitType(VisitType visitType) {
    return repository.findByVisitType(visitType)
        .orElseThrow(() -> new LocationException(LocationErrorCode.LOCATION_NOT_FOUND,
            "Nessuna location associata al tipo di visita con id: " + visitType.getId()));
  }
}
