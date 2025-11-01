package it.unibs.ingsw.destinazioni.application.services;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import it.unibs.ingsw.destinazioni.application.exceptions.codes.UserErrorCode;
import it.unibs.ingsw.destinazioni.application.exceptions.codes.VisitTypeErrorCode;
import it.unibs.ingsw.destinazioni.application.exceptions.usecases.UserException;
import it.unibs.ingsw.destinazioni.application.exceptions.usecases.VisitTypeException;
import it.unibs.ingsw.destinazioni.application.port.in.visittype.AssignVolunteerToVisitTypeUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.visittype.VisitTypeCommandUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.visittype.VisitTypeQueryUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.visittype.VisitTypeValidationUseCase;
import it.unibs.ingsw.destinazioni.application.port.out.LocationRepositoryPort;
import it.unibs.ingsw.destinazioni.application.port.out.UserRepositoryPort;
import it.unibs.ingsw.destinazioni.application.port.out.VisitPlanStatePort;
import it.unibs.ingsw.destinazioni.application.port.out.VisitTypeRepositoryPort;
import it.unibs.ingsw.destinazioni.application.port.out.VolunteerAvailabilityStatePort;
import it.unibs.ingsw.destinazioni.domain.model.Location;
import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.model.VisitType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VisitTypeService implements VisitTypeCommandUseCase, VisitTypeQueryUseCase, VisitTypeValidationUseCase,
    AssignVolunteerToVisitTypeUseCase {

  private final VisitTypeRepositoryPort repository;
  private final LocationRepositoryPort locationRepository;
  private final UserRepositoryPort userRepository;
  private final VolunteerAvailabilityStatePort volunteerAvailabilityStateRepo;
  private final VisitPlanStatePort visitPlanStateRepo;

  private final Clock clock;

  @Override
  public void addVisitType(VisitType visitType, int locationId) {

    if (repository.findByLocationId(locationId).stream().anyMatch(vt -> vt.getTitle().equals(visitType.getTitle()))) {
      throw new VisitTypeException(VisitTypeErrorCode.ALREADY_EXISTS,
          "Esiste già un tipo di visita con questo titolo.");
    }
    repository.save(visitType, locationId);
  }

  @Override
  public void removeVisitType(int visitTypeId) {

    VisitType visitType = repository.findById(visitTypeId)
        .orElseThrow(() -> new VisitTypeException(VisitTypeErrorCode.NOT_FOUND,
            "Visit Type con id " + visitTypeId + " non trovata"));

    if (!canBeRemoved(visitType.getId())) {
      throw new IllegalArgumentException("Il tipo di visita non può essere rimosso.");
    }

    Location location = locationRepository.findByVisitType(visitType)
        .orElseThrow(() -> new VisitTypeException(VisitTypeErrorCode.INVALID_LOCATION_FOR_VISIT_TYPE,
            "Location associata al tipo di visita non trovata"));

    location.removeVisitType(visitType);
    locationRepository.save(location);


    removeLocationIfItDoesNotHaveAnyVisitTypeLeft(location);
    removeVolunteersIfTheyAreNotAssignedToAnyOtherVisitType(visitType);
  }

  private void removeVolunteersIfTheyAreNotAssignedToAnyOtherVisitType(VisitType visitType) {
    if (visitType.getVolunteers() != null) {
      visitType.getVolunteers().forEach(volunteer -> {

        if (repository.findByVolunteerId(volunteer.getId()).isEmpty())
          userRepository.deleteById(volunteer.getId());

      });
    }
  }

  private void removeLocationIfItDoesNotHaveAnyVisitTypeLeft(Location location) {
    if (location.getVisitTypes() == null || location.getVisitTypes().isEmpty()) {
      locationRepository.deleteById(location.getId());
    }
  }

  @Override
  public Set<VisitType> listAll() {
    return repository.findAll();
  }

  @Override
  public Set<VisitType> listByLocation(int locationId) {
    return repository.findByLocationId(locationId);
  }

  @Override
  public Set<VisitType> listByVolunteerId(int volunteerId) {
    return repository.findByVolunteerId(volunteerId);
  }

  @Override

  public Optional<VisitType> findById(int id) {
    return repository.findById(id);
  }

  @Override
  public void updateVisitType(VisitType visitType) {
    if (visitType == null || visitType.getId() == null) {
      throw new VisitTypeException(VisitTypeErrorCode.DOES_NOT_EXIST, "VisitType o ID non valido");
    }

    var existing = repository.findById(visitType.getId());
    if (existing.isEmpty()) {
      throw new VisitTypeException(VisitTypeErrorCode.NOT_FOUND,
          "Visit Type con id " + visitType.getId() + " non trovata");
    }

    Location location = locationRepository.findByVisitType(visitType)
        .orElseThrow(() -> new VisitTypeException(VisitTypeErrorCode.INVALID_LOCATION_FOR_VISIT_TYPE,
            "Location associata al tipo di visita non trovata"));

    repository.save(visitType, location.getId());
  }

  @Override
  public boolean isAddOrRemovalStateActive() {
    LocalDate today = LocalDate.now(clock);

    boolean isTodayAfter15th = today.getDayOfMonth() > 15;

    LocalDate nextMonth = today.plusMonths(1);
    boolean isVisitPlanCreatedForNextMonth = visitPlanStateRepo.isVisitPlanCreated(nextMonth.getMonthValue(), nextMonth.getYear());

    LocalDate twoMonthsLater = today.plusMonths(2);
    boolean isAvailabilityOpenForTwoMonthsLater = volunteerAvailabilityStateRepo
        .isVolunteerAvailabilityOpen(twoMonthsLater.getMonthValue(), twoMonthsLater.getYear());

    return isTodayAfter15th && isVisitPlanCreatedForNextMonth && !isAvailabilityOpenForTwoMonthsLater;
  }

  @Override
  public boolean canBeRemoved(int visitTypeId) {
    LocalDate today = LocalDate.now(clock);
    VisitType visitType = repository.findById(visitTypeId)
        .orElseThrow(() -> new VisitTypeException(VisitTypeErrorCode.NOT_FOUND,
            "Visit Type con id " + visitTypeId + " non trovata"));

    LocalDate startDate = visitType.getStartDate();

    boolean isStartDateInFuture = startDate.isAfter(today.withDayOfMonth(1).plusMonths(2));

    return isAddOrRemovalStateActive() && isStartDateInFuture;
  }

  @Override
  public boolean canBeModified(int visitTypeId) {

    VisitType visitType = repository.findById(visitTypeId)
        .orElseThrow(() -> new VisitTypeException(VisitTypeErrorCode.NOT_FOUND,
            "Visit Type con id " + visitTypeId + " non trovata"));

    boolean isVisitPlanCreated = visitPlanStateRepo.isVisitPlanCreated(visitType.getStartDate().getMonthValue(),
        visitType.getStartDate().getYear());

    // NB: se il tipo di visita non può essere rimosso, non può essere modificato
    // perché non siamo nel periodo corretto
    boolean canBeRemoved = canBeRemoved(visitTypeId);
    return canBeRemoved && !isVisitPlanCreated;
  }

  @Override
  public void addVolunteerToVisitType(int visitTypeId, String nickname) {

    if (!this.canBeModified(visitTypeId)) {
      throw new VisitTypeException(VisitTypeErrorCode.CANT_BE_MODIFIED, "Il tipo di visita non può essere modificato.");
    }

    VisitType visitType = repository.findById(visitTypeId)
        .orElseThrow(() -> new VisitTypeException(VisitTypeErrorCode.NOT_FOUND,
            "Visit Type con id " + visitTypeId + " non trovata"));

    User volunteer = userRepository.findByNickname(nickname).orElseThrow(
        () -> new UserException(UserErrorCode.USER_NOT_FOUND, "Volontario con nickname " + nickname + " non trovato"));

    boolean alreadyPresent = visitType.getVolunteers().stream().anyMatch(v -> v.getNickname().equals(nickname));

    if (alreadyPresent) {
      throw new VisitTypeException(VisitTypeErrorCode.VOLUNTEER_ALREADY_ASSIGNED,
          "Il volontario è già associato a questo tipo di visita.");
    }

    List<User> volunteers = new ArrayList<>(visitType.getVolunteers());
    volunteers.add(volunteer);
    visitType.setVolunteers(volunteers);

    this.updateVisitType(visitType);
  }

}
