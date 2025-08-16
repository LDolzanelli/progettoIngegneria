package it.unibs.ingsw.destinazioni.application.service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import it.unibs.ingsw.destinazioni.application.port.in.ManageVisitTypeUseCase;
import it.unibs.ingsw.destinazioni.application.port.out.LocationRepositoryPort;
import it.unibs.ingsw.destinazioni.application.port.out.UserRepositoryPort;
import it.unibs.ingsw.destinazioni.application.port.out.VisitPlanStatePort;
import it.unibs.ingsw.destinazioni.application.port.out.VisitTypeRepositoryPort;
import it.unibs.ingsw.destinazioni.application.port.out.VolunteerAvailabilityStatePort;
import it.unibs.ingsw.destinazioni.domain.model.Location;
import it.unibs.ingsw.destinazioni.domain.model.VisitType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VisitTypeService implements ManageVisitTypeUseCase {

    private final VisitTypeRepositoryPort repository;
    private final LocationRepositoryPort locationRepository;
    private final UserRepositoryPort userRepository;
    private final VolunteerAvailabilityStatePort volunteerAvailabilityStateRepo;
    private final VisitPlanStatePort visitPlanStateRepo;

    private final Clock clock;



    @Override
    public void addVisitType(VisitType visitType, int locationId) {
        repository.save(visitType, locationId);
    }


    @Override
    public void removeVisitType(int visitTypeId) {

        VisitType visitType = repository.findById(visitTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Visit Type con id " + visitTypeId + " non trovata"));


        if (!canBeRemoved(visitType.getId())) {
            throw new IllegalArgumentException("Il tipo di visita non può essere rimosso.");
        }



        Location location = locationRepository.findByVisitType(visitType)
                .orElseThrow(() -> new IllegalArgumentException("Location associata al tipo di visita non trovata"));



        location.removeVisitType(visitType);
        locationRepository.save(location);

        // Se dopo la rimozione del tipo di visita non ci sono più tipi di visita associati alla location,
        // possiamo rimuovere la location
        if (location.getVisitTypes() == null || location.getVisitTypes().isEmpty()) {
            locationRepository.deleteById(location.getId());
        }

        // Rimuovere i volontari che erano associati a questo tipo di visita se non sono associati ad
        // altri tipi di visita
        if (visitType.getVolunteers() != null) {
            visitType.getVolunteers().forEach(volunteer -> {

                if (repository.findByVolunteerId(volunteer.getId()).isEmpty())
                    userRepository.deleteById(volunteer.getId());

            });
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


    @Override
    public boolean canBeRemoved(int visitTypeId) {
        LocalDate today = LocalDate.now(clock);
        VisitType visitType = repository.findById(visitTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Visit Type non trovata"));

        LocalDate startDate = visitType.getStartDate();
        LocalDate endDate = visitType.getEndDate();



        //verifica se siamo dopo il 15 del mese corrente (i)
        boolean isAfter15th = today.getDayOfMonth() > 15;

        //verifica se è stato prodotto il piano visite per i+1
        LocalDate nextMonth = today.plusMonths(1);
        boolean isVisitPlanCreated =
                visitPlanStateRepo.isVisitPlanCreated(nextMonth.getMonthValue(), nextMonth.getYear());

        //verifica se non è stata aperta la raccolta disponibilità per i+2
        LocalDate twoMonthsLater = today.plusMonths(2);
        boolean isAvailabilityOpen = volunteerAvailabilityStateRepo
                .isVolunteerAvailabilityOpen(twoMonthsLater.getMonthValue(), twoMonthsLater.getYear());

        //verifica se la data di inizio è nel futuro rispetto al mese corrente (i+2) o se la data di fine è nel passato rispetto al mese corrente (i)
        boolean isStartDateInFuture = startDate.isAfter(today.withDayOfMonth(1).plusMonths(1)); 
        boolean isEndDateInPast = endDate.isBefore(today.withDayOfMonth(1));

        return isAfter15th && isVisitPlanCreated && !isAvailabilityOpen && (isStartDateInFuture || isEndDateInPast);
    }



}
