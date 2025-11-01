package it.unibs.ingsw.destinazioni.application.services;

import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.unibs.ingsw.destinazioni.application.exceptions.codes.UserErrorCode;
import it.unibs.ingsw.destinazioni.application.exceptions.codes.VisitPlanErrorCode;
import it.unibs.ingsw.destinazioni.application.exceptions.usecases.UserException;
import it.unibs.ingsw.destinazioni.application.exceptions.usecases.VisitPlanException;
import it.unibs.ingsw.destinazioni.application.port.in.user.GetUserInfoUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.visitplan.CreateVisitPlanUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.visitplan.VisitPlanQueryUseCase;
import it.unibs.ingsw.destinazioni.application.port.out.BlockedDatesRepositoryPort;
import it.unibs.ingsw.destinazioni.application.port.out.VisitPlanStatePort;
import it.unibs.ingsw.destinazioni.application.port.out.VisitRepositoryPort;
import it.unibs.ingsw.destinazioni.application.port.out.VisitTypeRepositoryPort;
import it.unibs.ingsw.destinazioni.application.port.out.VolunteerAvailabilityStatePort;
import it.unibs.ingsw.destinazioni.application.port.out.VolunteerAvailableDateRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.model.Visit;
import it.unibs.ingsw.destinazioni.domain.model.VisitType;
import it.unibs.ingsw.destinazioni.domain.model.VolunteerAvailableDate;
import it.unibs.ingsw.destinazioni.domain.model.enums.Role;
import it.unibs.ingsw.destinazioni.domain.model.enums.VisitStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Service
@RequiredArgsConstructor
public class VisitPlanService implements CreateVisitPlanUseCase, VisitPlanQueryUseCase {

    private final VisitPlanStatePort statePort;
    private final VolunteerAvailabilityStatePort availabilityStatePort;
    private final VisitTypeRepositoryPort visitTypeRepository;
    private final Clock clock;

    private final GetUserInfoUseCase userInfoUseCase;
    private final VolunteerAvailableDateRepositoryPort volunteerAvailabilityRepository;
    private final VisitRepositoryPort visitRepository;
    private final BlockedDatesRepositoryPort blockedDatesRepository;

    /*
     * controlla se il piano di visita può essere creato.
     * Il piano per il mese i+1 (i = mese attuale) può essere creato se:
     * - il piano per il mese i non è stato ancora creato
     * - la raccolta di disponibilità per il mese i+1 è stata chiusa
     */
    @Override
    public boolean canCreateVisitPlan() {
        LocalDate today = LocalDate.now(clock);
        YearMonth nextMonth = YearMonth.from(today).plusMonths(1);
        int nextMonthValue = nextMonth.getMonthValue();
        int nextYear = nextMonth.getYear();

        return !statePort.isVisitPlanCreated(nextMonthValue, nextYear)
                && !availabilityStatePort.isVolunteerAvailabilityOpen(nextMonthValue, nextYear);
    }

    @Transactional // per poter rimuovere le blockedDates
    @Override
    public void createVisitPlan() {
        if (!canCreateVisitPlan()) {
            throw new VisitPlanException(VisitPlanErrorCode.CANT_BE_CREATED,
                    "Non è possibile creare il piano di visita per il mese successivo.");
        }

        YearMonth nextMonth = YearMonth.from(LocalDate.now(clock)).plusMonths(1);

        List<VolunteerAvailableDate> volunteerAvailabilitiesNextMonth = volunteerAvailabilityRepository
                .findByYearMonth(nextMonth);

        List<Visit> nextMonthSortedVisits = getNextMonthVisitsSorted(nextMonth.getMonthValue(), nextMonth.getYear());

        List<VolunteerIdWithVisitCount> volunteerIdWithVisitCounts = initializeVolunteerWithVisitCount();

        for (Visit visit : nextMonthSortedVisits) {
            List<Integer> availableVolunteerIds = getAvailableVolunteersForVisitDate(visit,
                    volunteerAvailabilitiesNextMonth);

            if (availableVolunteerIds.isEmpty()) {
                setVisitStatusToCancelled(visit);
            } else if (availableVolunteerIds.size() == 1) {
                assignAvailableVolunteerToVisitIfPossible(visit, availableVolunteerIds,
                        volunteerAvailabilitiesNextMonth, volunteerIdWithVisitCounts);
            } else {
                assignBestVolunteerAvailableToVisit(visit, availableVolunteerIds, volunteerIdWithVisitCounts,
                        volunteerAvailabilitiesNextMonth);
            }
        }
        removeBlockedDatesFromSelectedMonth(nextMonth);

        statePort.setVisitPlanCreated(nextMonth.getMonthValue(), nextMonth.getYear(), true);
    }

    private void assignBestVolunteerAvailableToVisit(Visit visit, List<Integer> availableVolunteerIds,
            List<VolunteerIdWithVisitCount> volunteerIdWithVisitCounts,
            List<VolunteerAvailableDate> volunteerAvailabilitiesNextMonth) {
        int bestVolunteerId = selectBestVolunteerId(availableVolunteerIds, volunteerIdWithVisitCounts);
        assignVolunteerToVisit(bestVolunteerId, visit, volunteerAvailabilitiesNextMonth, volunteerIdWithVisitCounts);
    }

    private void assignAvailableVolunteerToVisitIfPossible(Visit visit, List<Integer> availableVolunteerIds,
            List<VolunteerAvailableDate> volunteerAvailabilitiesNextMonth,
            List<VolunteerIdWithVisitCount> volunteerIdWithVisitCounts) {
        int availableVolunteer = availableVolunteerIds.getFirst();
        if (canDoVisitType(availableVolunteer, visit.getVisitType().getId()))
            assignVolunteerToVisit(availableVolunteer, visit, volunteerAvailabilitiesNextMonth,
                    volunteerIdWithVisitCounts);
        else {
            setVisitStatusToCancelled(visit);
        }
    }

    private void setVisitStatusToCancelled(Visit visit) {
        visit.setVisitStatus(VisitStatus.CANCELLED);
        visitRepository.save(visit);
    }

    public void removeBlockedDatesFromSelectedMonth(YearMonth nextMonth) {
        blockedDatesRepository.loadByMonth(nextMonth.getMonthValue(), nextMonth.getYear()) //
                .getDates() //
                .forEach(blockedDatesRepository::delete);
    }

    private List<Integer> getAvailableVolunteersForVisitDate(Visit visit,
            List<VolunteerAvailableDate> volunteerAvailabilitiesNextMonth) {
        return volunteerAvailabilitiesNextMonth.stream() //
                .filter(volunteerAvailableDate -> volunteerAvailableDate.getAvailableDate().equals(visit.getDate())) //
                .map(VolunteerAvailableDate::getVolunteerId) //
                .collect(Collectors.toList());
    }

    @Override
    public List<Visit> getVisitPlan(int month, int year) {
        ArrayList<Visit> visitPlan = new ArrayList<>();

        visitRepository.findAll().stream()
                .filter(visit -> visit.getDate().getMonthValue() == month && visit.getDate().getYear() == year)
                .forEach(visitPlan::add);

        return visitPlan;
    }

    @Override
    public List<Visit> getAllVisitsAfterToday() {
        ArrayList<Visit> visitPlan = new ArrayList<>();
        LocalDate today = LocalDate.now(clock);

        Set<Visit> allVisits = visitRepository.findAll();

        allVisits.stream().filter(visit -> visit.getVolunteer() != null) // ignora le visite inizializzate, ma non
                                                                         // ancora parte del piano
                .filter(visit -> visit.getDate().isAfter(today))
                .filter(visit -> visit.getVisitStatus() != VisitStatus.COMPLETED) // ignora archivio storico
                .forEach(visitPlan::add);

        return visitPlan;
    }

    @Override
    public List<Visit> getAllCompletedVisits() {
        ArrayList<Visit> visitPlan = new ArrayList<>();

        visitRepository.findAll().stream().filter(visit -> visit.getVisitStatus() == VisitStatus.COMPLETED)
                .forEach(visitPlan::add);

        return visitPlan;
    }

    @Override
    public int getMonth() {
        LocalDate today = LocalDate.now(clock);
        return YearMonth.from(today).plusMonths(1).getMonthValue();
    }

    @Override
    public int getYear() {
        LocalDate today = LocalDate.now(clock);
        return today.getYear();
    }

    @Getter
    @Setter
    private class VolunteerIdWithVisitCount {
        private int volunteerId;
        private int visitCount = 0;

        public VolunteerIdWithVisitCount(int volunteerId) {
            this.volunteerId = volunteerId;
        }

        public void increment() {
            visitCount++;
        }
    }

    private int visitCountFromVolunteer(int volunteerId, List<VolunteerIdWithVisitCount> volunteerIdWithVisitCounts) {
        for (VolunteerIdWithVisitCount volunteerIdVisitCount : volunteerIdWithVisitCounts) {
            if (volunteerIdVisitCount.getVolunteerId() == volunteerId) {
                return volunteerIdVisitCount.getVisitCount();
            }
        }

        return -1;
    }

    private User volunteerById(int id, List<User> volunteers) {
        Optional<User> volunteer = volunteers.stream() //
                .filter(user -> user.getId() == id) //
                .findAny();

        if (volunteer.isPresent())
            return volunteer.get();
        else
            throw new UserException(UserErrorCode.USER_NOT_FOUND,
                    "Volunteer with id " + id + " not found in volunteer list!");
    }

    private boolean canDoVisitType(int volunteerId, int visitTypeId) {
        Set<Integer> visitTypesIdsAssociatedToVolunteer = visitTypeRepository.findByVolunteerId(volunteerId).stream() //
                .map(VisitType::getId) //
                .collect(Collectors.toSet());
        return visitTypesIdsAssociatedToVolunteer.contains(visitTypeId);
    }

    private int selectBestVolunteerId(List<Integer> volunteerIds,
            List<VolunteerIdWithVisitCount> volunteerIdWithVisitCounts) {
        Comparator<Integer> volunteerComparator = //
                (volunteer1, volunteer2) -> //
                Integer.compare(visitCountFromVolunteer(volunteer1, volunteerIdWithVisitCounts), //
                        visitCountFromVolunteer(volunteer2, volunteerIdWithVisitCounts));

        return volunteerIds.stream() //
                .min(volunteerComparator) //
                .orElseThrow(() -> new VisitPlanException(VisitPlanErrorCode.NO_AVAILABLE_VOLUNTEERS,
                        "No volunteers available"));

    }

    /*
     * Assegna un volontario a una visita
     * 1) imposta il volontario nel campo volunteer della visita
     * 2) mette la visita in stato PROPOSED
     * 3) incrementa il conteggio delle visite del volontario
     * 4) rimuove la disponibilitá utilizzata per coprire la visita
     * 5) salva la visita
     */
    private void assignVolunteerToVisit(int volunteerId, Visit visit,
            List<VolunteerAvailableDate> volunteerAvailabilities,
            List<VolunteerIdWithVisitCount> volunteerIdWithVisitCounts) {
        User volunteer = userInfoUseCase.findById(volunteerId).orElse(null);
        visit.setVolunteer(volunteer);
        visit.setVisitStatus(VisitStatus.PROPOSED);

        volunteerIdWithVisitCounts.stream() //
                .filter(volunteerVisitCount -> Objects.equals(volunteerVisitCount.getVolunteerId(), volunteerId)) //
                .findFirst() //
                .orElseThrow(() -> new VisitPlanException(VisitPlanErrorCode.AVAILABILITY_NOT_FOUND,
                        "Volunteer not found in visit counts")) //
                .increment();

        VolunteerAvailableDate usedAvailability = volunteerAvailabilities.stream().filter(Objects::nonNull) //
                .filter(volunteerAvailableDate -> Objects.equals(volunteerAvailableDate.getVolunteerId(), volunteerId) //
                        && volunteerAvailableDate.getAvailableDate().equals(visit.getDate())) //
                .findFirst() //
                .orElseThrow(() -> new VisitPlanException(VisitPlanErrorCode.AVAILABILITY_NOT_FOUND,
                        "Volunteer not found in visit counts"));

        volunteerAvailabilities.remove(usedAvailability);

        visitRepository.save(visit);
    }

    private List<Visit> getNextMonthVisitsSorted(int month, int year) {
        List<Visit> monthVisits = new ArrayList<>();

        visitRepository.findAll().stream() //
                .filter(visit -> visit.getDate().getMonthValue() == month && visit.getDate().getYear() == year)
                .forEach(monthVisits::add);

        Comparator<Visit> visitCompare = Comparator.comparing(Visit::getDate);
        monthVisits.sort(visitCompare);

        return monthVisits;
    }

    private List<VolunteerIdWithVisitCount> initializeVolunteerWithVisitCount() {
        List<Integer> volunteerIds = userInfoUseCase.getUsersIdsByRole(Role.VOLUNTEER);
        List<VolunteerIdWithVisitCount> volunteerIdWithVisitCounts = new ArrayList<>();

        for (int volunteerId : volunteerIds) {
            volunteerIdWithVisitCounts.add(new VolunteerIdWithVisitCount(volunteerId));
        }

        return volunteerIdWithVisitCounts;
    }

}
