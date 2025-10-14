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
import it.unibs.ingsw.destinazioni.application.exceptions.specific.UserException;
import it.unibs.ingsw.destinazioni.application.exceptions.specific.VisitTypeException;
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
import it.unibs.ingsw.destinazioni.domain.model.Visit;
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
  /*@ also
    @ requires repository != null && locationRepository != null;
    @ ensures repository.findById(visitType.getId()) != null ==> 
    @         repository.findById(visitType.getId()).isPresent();
    @*/
  public void addVisitType(VisitType visitType, int locationId) {


    if (repository.findByLocationId(locationId).stream().anyMatch(vt -> vt.getTitle().equals(visitType.getTitle()))) {
      throw new VisitTypeException(VisitTypeErrorCode.ALREADY_EXISTS,
          "Esiste già un tipo di visita con questo titolo.");
    }
    repository.save(visitType, locationId);
  }


  @Override
  /*@ also
    @ ensures !repository.findById(visitTypeId).isPresent();
    @ ensures (\forall User volunteer; \old(repository.findById(visitTypeId).get().getVolunteers()).contains(volunteer);
    @          repository.findByVolunteerId(volunteer.getId()).isEmpty() ==> 
    @          !userRepository.findById(volunteer.getId()).isPresent());
    @*/
  public void removeVisitType(int visitTypeId) {

    VisitType visitType =
        repository.findById(visitTypeId).orElseThrow(() -> new VisitTypeException(VisitTypeErrorCode.NOT_FOUND,
            "Visit Type con id " + visitTypeId + " non trovata"));

    if (!canBeRemoved(visitType.getId())) {
      throw new IllegalArgumentException("Il tipo di visita non può essere rimosso.");
    }

    Location location = locationRepository.findByVisitType(visitType)
        .orElseThrow(() -> new VisitTypeException(VisitTypeErrorCode.INVALID_LOCATION_FOR_VISIT_TYPE,
            "Location associata al tipo di visita non trovata"));

    location.removeVisitType(visitType);
    locationRepository.save(location);

    // Se dopo la rimozione del tipo di visita non ci sono più tipi di visita
    // associati alla location,
    // possiamo rimuovere la location
    if (location.getVisitTypes() == null || location.getVisitTypes().isEmpty()) {
      locationRepository.deleteById(location.getId());
    }

    // Rimuovere i volontari che erano associati a questo tipo di visita se non sono
    // associati ad
    // altri tipi di visita
    if (visitType.getVolunteers() != null) {
      visitType.getVolunteers().forEach(volunteer -> {

        if (repository.findByVolunteerId(volunteer.getId()).isEmpty())
          userRepository.deleteById(volunteer.getId());

      });
    }
  }


  @Override
  /*@ also
    @ ensures \result.equals(repository.findAll());
    @*/
  public Set<VisitType> listAll() {
    return repository.findAll();
  }


  @Override
  /*@ also
    @ ensures \result.equals(repository.findByLocationId(locationId));
    @*/
  public Set<VisitType> listByLocation(int locationId) {
    return repository.findByLocationId(locationId);
  }


  @Override
  /*@ also
    @ ensures \result.equals(repository.findByVolunteerId(volunteerId));
    @*/
  public Set<VisitType> listByVolunteerId(int volunteerId) {
    return repository.findByVolunteerId(volunteerId);
  }


  @Override
  /*@ also
    @ ensures \result.equals(repository.findById(id));
    @*/
  public Optional<VisitType> findById(int id) {
    return repository.findById(id);
  }


  @Override
  /*@ also
    @ ensures repository.findById(visitType.getId()).isPresent();
    @ ensures repository.findById(visitType.getId()).get().getTitle().equals(visitType.getTitle());
    @ ensures repository.findById(visitType.getId()).get().getDescription().equals(visitType.getDescription());
    @*/
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
  /*@ also
    @ requires clock != null && volunteerAvailabilityStateRepo != null && visitPlanStateRepo != null;
    @ ensures \result == (LocalDate.now(clock).getDayOfMonth() > 15 &&
    @                    visitPlanStateRepo.isVisitPlanCreated(LocalDate.now(clock).plusMonths(1).getMonthValue(),
    @                                                         LocalDate.now(clock).plusMonths(1).getYear()) &&
    @                    !volunteerAvailabilityStateRepo.isVolunteerAvailabilityOpen(
    @                        LocalDate.now(clock).plusMonths(2).getMonthValue(),
    @                        LocalDate.now(clock).plusMonths(2).getYear()));
    @*/
  public boolean isAddOrRemovalStateActive() {
    LocalDate today = LocalDate.now(clock);

    // verifica se siamo dopo il 15 del mese corrente (i)
    boolean isAfter15th = today.getDayOfMonth() > 15;

    // verifica se è stato prodotto il piano visite per i+1
    LocalDate nextMonth = today.plusMonths(1);
    boolean isVisitPlanCreated = visitPlanStateRepo.isVisitPlanCreated(nextMonth.getMonthValue(), nextMonth.getYear());

    // verifica se non è stata aperta la raccolta disponibilità per i+2
    LocalDate twoMonthsLater = today.plusMonths(2);
    boolean isAvailabilityOpen = volunteerAvailabilityStateRepo
        .isVolunteerAvailabilityOpen(twoMonthsLater.getMonthValue(), twoMonthsLater.getYear());

    return isAfter15th && isVisitPlanCreated && !isAvailabilityOpen;
  }


  @Override
  /*@ also
    @ ensures \result ==> isAddOrRemovalStateActive();
    @ ensures \result == (isAddOrRemovalStateActive() && 
    @                    (repository.findById(visitTypeId).get().getStartDate()
    @                     .isAfter(LocalDate.now(clock).withDayOfMonth(1).plusMonths(2));
    @*/
  public boolean canBeRemoved(int visitTypeId) {
    LocalDate today = LocalDate.now(clock);
    VisitType visitType =
        repository.findById(visitTypeId).orElseThrow(() -> new VisitTypeException(VisitTypeErrorCode.NOT_FOUND,
            "Visit Type con id " + visitTypeId + " non trovata"));

    LocalDate startDate = visitType.getStartDate();

    // verifica se la data di inizio è nel futuro rispetto al mese corrente (i+2) 
    boolean isStartDateInFuture = startDate.isAfter(today.withDayOfMonth(1).plusMonths(2));

    return isAddOrRemovalStateActive() && isStartDateInFuture;
  }


  @Override
  /*@ also
    @ ensures \result ==> canBeRemoved(visitTypeId);
    @ ensures \result == (canBeRemoved(visitTypeId) && 
    @                    !visitPlanStateRepo.isVisitPlanCreated(
    @                        repository.findById(visitTypeId).get().getStartDate().getMonthValue(),
    @                        repository.findById(visitTypeId).get().getStartDate().getYear()));
    @*/
  public boolean canBeModified(int visitTypeId) {

    VisitType visitType =
        repository.findById(visitTypeId).orElseThrow(() -> new VisitTypeException(VisitTypeErrorCode.NOT_FOUND,
            "Visit Type con id " + visitTypeId + " non trovata"));

    boolean isVisitPlanCreated = visitPlanStateRepo.isVisitPlanCreated(visitType.getStartDate().getMonthValue(),
        visitType.getStartDate().getYear());

    // NB: se il tipo di visita non può essere rimosso, non può essere modificato
    // perché non siamo nel
    // periodo corretto
    boolean canBeRemoved = canBeRemoved(visitTypeId);
    return canBeRemoved && !isVisitPlanCreated;
  }


  @Override
  /*@ also
    @ ensures repository.findById(visitTypeId).get().getVolunteers().stream()
    @         .anyMatch(v -> v.getNickname().equals(nickname));
    @ ensures repository.findById(visitTypeId).get().getVolunteers().size() == 
    @         \old(repository.findById(visitTypeId).get().getVolunteers().size()) + 1;
    @*/
  public void addVolunteerToVisitType(int visitTypeId, String nickname) {

    if (!this.canBeModified(visitTypeId)) {
      throw new VisitTypeException(VisitTypeErrorCode.CANT_BE_MODIFIED, "Il tipo di visita non può essere modificato.");
    }

    VisitType visitType =
        repository.findById(visitTypeId).orElseThrow(() -> new VisitTypeException(VisitTypeErrorCode.NOT_FOUND,
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
