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
  /*
   * @ also
   * 
   * @ requires repository != null;
   * 
   * @ ensures repository.findById(location.getId()) != null ==>
   * repository.findById(location.getId()).isPresent();
   * 
   * @
   */
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
  /*
   * @ also
   * 
   * @ ensures !repository.findById(locationId).isPresent() ||
   * 
   * @ (repository.findById(locationId).get().getVisitTypes() != null &&
   * 
   * @ repository.findById(locationId).get().getVisitTypes().isEmpty());
   * 
   * @
   */
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
      // eventuali volontari
      // associati. La location verrà eliminata dopo la rimozione dei tipi di visita
      location.getVisitTypes().forEach(visitType -> visitTypeCommands.removeVisitType(visitType.getId()));
    }
  }

  @Override
  /*
   * @ also
   * 
   * @ ensures \result.equals(repository.findAll());
   * 
   * @
   */
  public List<Location> listAll() {
    return repository.findAll();
  }

  @Override
  /*
   * @ also
   * 
   * @ ensures \result.equals(repository.findById(id));
   * 
   * @
   */
  public Optional<Location> findById(int id) {
    return repository.findById(id);
  }

  @Override
  /*
   * @ also
   * 
   * @ ensures repository.findById(location.getId()).isPresent();
   * 
   * @ ensures
   * repository.findById(location.getId()).get().getName().equals(location.getName
   * ());
   * 
   * @ ensures
   * repository.findById(location.getId()).get().getDescription().equals(location.
   * getDescription());
   * 
   * @
   */
  public void updateLocation(Location location) {

    if (location == null || location.getId() == null) {
      throw new LocationException(LocationErrorCode.NULL_LOCATION, "Location o ID della location null");
    }

    var existing = repository.findById(location.getId());
    if (existing.isEmpty()) {
      throw new LocationException(LocationErrorCode.LOCATION_NOT_FOUND,
          "La località con id " + location.getId() + " non trovata");
    }

    repository.save(location);
  }

  @Override
  /*
   * @ also
   * 
   * @ ensures \result == (repository.findById(locationId).get().getVisitTypes()
   * == null ||
   * 
   * @ repository.findById(locationId).get().getVisitTypes().isEmpty() ||
   * 
   * @ (\forall VisitType vt;
   * repository.findById(locationId).get().getVisitTypes().contains(vt);
   * 
   * @ visitTypeValidation.canBeRemoved(vt.getId())));
   * 
   * @
   */
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
  /*
   * @ also
   * 
   * @ ensures \result.equals(repository.findByVisitType(visitType).orElseThrow(()
   * ->
   * 
   * @ new
   * IllegalArgumentException("Nessuna location associata al tipo di visita con id: "
   * + visitType.getId())));
   * 
   * @
   */
  public Location getLocationForVisitType(VisitType visitType) {
    return repository.findByVisitType(visitType)
        .orElseThrow(() -> new LocationException(LocationErrorCode.LOCATION_NOT_FOUND,
            "Nessuna location associata al tipo di visita con id: " + visitType.getId()));
  }
}
