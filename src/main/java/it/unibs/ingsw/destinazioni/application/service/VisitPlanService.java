package it.unibs.ingsw.destinazioni.application.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.unibs.ingsw.destinazioni.application.port.in.GetUserInfoUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.VisitPlanUseCase;
import it.unibs.ingsw.destinazioni.application.port.out.VisitPlanStatePort;
import it.unibs.ingsw.destinazioni.application.port.out.VolunteerAvailabilityStatePort;
import it.unibs.ingsw.destinazioni.domain.model.Visit;
import it.unibs.ingsw.destinazioni.domain.model.enums.VisitStatus;
import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.model.VisitType;
import it.unibs.ingsw.destinazioni.domain.model.VolunteerAvailableDate;
import it.unibs.ingsw.destinazioni.domain.model.BlockedDates;

import it.unibs.ingsw.destinazioni.application.port.out.VisitTypeRepositoryPort;
import it.unibs.ingsw.destinazioni.application.port.out.VolunteerAvailableDateRepositoryPort;
import it.unibs.ingsw.destinazioni.application.port.out.VisitRepositoryPort;
import it.unibs.ingsw.destinazioni.application.port.out.BlockedDatesRepositoryPort;

import it.unibs.ingsw.destinazioni.domain.model.enums.Role;

import lombok.RequiredArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Service
@RequiredArgsConstructor
public class VisitPlanService implements VisitPlanUseCase {

    private final VisitPlanStatePort statePort;
    private final VolunteerAvailabilityStatePort availabilityStatePort;
    private final VisitTypeRepositoryPort visitTypeRepository;
    private final Clock clock;

    private final GetUserInfoUseCase userService;
    private final VolunteerAvailableDateRepositoryPort volunteerAvailabilityRepository;
    private final VisitRepositoryPort visitRepository;
    private final BlockedDatesRepositoryPort blockedDatesRepository;

    /**
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
            throw new IllegalStateException("Non è possibile creare il piano di visita per il mese successivo.");
        }

        LocalDate today = LocalDate.now(clock);
        YearMonth nextMonth = YearMonth.from(today).plusMonths(1);
        int nextMonthValue = nextMonth.getMonthValue();
        int nextYear = nextMonth.getYear();


        List<User> volunteers = userService.getUsersByRole(Role.VOLUNTEER);


        List<VisitType> visitTypes = new ArrayList();
        visitTypes.addAll(visitTypeRepository.findAll());

        List<VolunteerAvailableDate> monthAvailabilities =
                initMonthAvailabilities(volunteers, nextMonthValue, nextYear);

        // recupera le visite del prossimo mese e ordinale
        List<Visit> monthVisits = initMonthVisits(nextMonthValue, nextYear);

        /*
         * Conteggio delle visite assegnate ai volontari nel mese considerato
         * Viene aggiornato ogni volta che viene assegnata una visita a un volontario
         */
        List<VolunteerVisitCount> volunteerVisitCounts = initVolunteerVisitCounts(volunteers);

        for (Visit visit : monthVisits) {
            LocalDate day = visit.getDate();
            List<User> availableVolunteers = new ArrayList<>();

            // aggiunge alla lista availableVolunteers tutti i volontari che hanno dato disponibilitá per il
            // giorno 'day'
            monthAvailabilities.stream().filter(av -> av.getAvailableDate().equals(day))
                    .map(av -> volunteerById(av.getVolunteerId(), volunteers)) // mappa le disponibilitá
                                                                               // (VolunteerAvailableDate) con gli
                                                                               // oggetti dei volontari (User di tipo
                                                                               // VOLUNTEER)
                    .forEach(availableVolunteers::add);


            if (availableVolunteers.isEmpty()) {
                visit.setVisitStatus(VisitStatus.CANCELLED);
                visitRepository.save(visit);
                continue; // passa alla visita successiva
            } else if (availableVolunteers.size() == 1) {
                User volunteer = availableVolunteers.getFirst();
                if (canDoVisitType(volunteer, visit.getVisitType()))
                    assignVolunteerToVisit(volunteer, visit, monthAvailabilities, volunteerVisitCounts);

                continue; // passa alla visita successiva
            }

            // piú di 2 volontari con disponibilitá nel giorno considerato
            User bestVolunteer = selectBestVolunteer(availableVolunteers, volunteerVisitCounts);
            assignVolunteerToVisit(bestVolunteer, visit, monthAvailabilities, volunteerVisitCounts);
        }

        //rimuove le blockedDates del mese considerato
        removeBlockedDates(nextMonthValue, nextYear);

        statePort.setVisitPlanCreated(nextMonthValue, nextYear, true);
    }


    @Override
    public List<Visit> getVisitPlan(int month, int year) {
        System.out.println("VisitPlan creation started");
        // TODO: implementare la logica per ottenere il piano di visita
        return List.of(); // restituisce una lista vuota per ora
    }


    @Override
    public int getMonth() {
        LocalDate today = LocalDate.now(clock);
        return YearMonth.from(today).plusMonths(1).getMonthValue();
    }

    @Getter
    @Setter
    private class VolunteerVisitCount {
        private User volunteer;
        private int visitCount = 0;

        public VolunteerVisitCount(User volunteer) {
            this.volunteer = volunteer;
        }


        public void increment() {
            visitCount++;
        }
    }

    private int visitCountFromVolunteer(User volunteer, List<VolunteerVisitCount> volunteerVisitCounts) {
        for (VolunteerVisitCount vvc : volunteerVisitCounts) {
            if (vvc.getVolunteer().equals(volunteer))
                return vvc.getVisitCount();
        }

        return -1;
    }


    private User volunteerById(int id, List<User> volunteers) {
        Optional<User> volunteer = volunteers.stream().filter(v -> v.getId() == id).findAny();
        if (volunteer.isPresent())
            return volunteer.get();
        else
            throw new RuntimeException("Volunteer with id " + id + " not found in volunteer list!");
    }


    private boolean canDoVisitType(User volunteer, VisitType visitType) {
        Set<VisitType> visitTypes = visitTypeRepository.findByVolunteerId(volunteer.getId());
        if (visitTypes.contains(visitType))
            return true;
        else
            return false;
    }

    private User selectBestVolunteer(List<User> volunteers, List<VolunteerVisitCount> volunteerVisitCounts) {

        Comparator<User> volunteerComparator =
                (v1, v2) -> Integer.compare(visitCountFromVolunteer(v1, volunteerVisitCounts),
                        visitCountFromVolunteer(v2, volunteerVisitCounts));

        return volunteers.stream().min(volunteerComparator).get(); // ritorna il volontario a cui sono state assegnate
                                                                   // meno visite
    }


    /*
     * Assegna un volontario a una visita
     * 1) imposta il volontario nel campo volunteer della visita
     * 2) mette la visita in stato PROPOSED
     * 3) incrementa il conteggio delle visite del volontario
     * 4) rimuove la disponibilitá utilizzata per coprire la visita
     * 5) salva la visita
     */
    private void assignVolunteerToVisit(User volunteer, Visit visit,
            List<VolunteerAvailableDate> volunteerAvailabilities, List<VolunteerVisitCount> volunteerVisitCounts) {
        visit.setVolunteer(volunteer);
        visit.setVisitStatus(VisitStatus.PROPOSED);
        volunteerVisitCounts.stream().filter(vvc -> vvc.getVolunteer().getId() == volunteer.getId()).findFirst().get()
                .increment();
        VolunteerAvailableDate usedAvailability = volunteerAvailabilities.stream().filter(Objects::nonNull)
                .filter(av -> av.getVolunteerId() == volunteer.getId() && av.getAvailableDate().equals(visit.getDate()))
                .findFirst().get();

        volunteerAvailabilities.remove(usedAvailability);

        visitRepository.save(visit);
    }


    private List<VolunteerAvailableDate> initMonthAvailabilities(List<User> volunteers, int month, int year) {
        List<VolunteerAvailableDate> monthAvailabilities = new ArrayList<>();
        for (User v : volunteers) {
            // per ogni volontario recupera le sue disponibilitá e seleziona quelle del mese selezionato
            List<VolunteerAvailableDate> volunteerAvailabilities = new ArrayList<>();

            volunteerAvailabilityRepository.findByVolunteerId(v.getId()).stream().filter(va -> {
                return va.getAvailableDate().getMonthValue() == month && va.getAvailableDate().getYear() == year;
            }).forEach(volunteerAvailabilities::add);

            if (!volunteerAvailabilities.isEmpty())
                monthAvailabilities.addAll(volunteerAvailabilities);
        }

        return monthAvailabilities;
    }


    private List<Visit> initMonthVisits(int month, int year) {
        List<Visit> monthVisits = new ArrayList<>();

        visitRepository.findAll().stream().filter(visit -> {
            return visit.getDate().getMonthValue() == month && visit.getDate().getYear() == year;
        }).forEach(monthVisits::add);

        Comparator visitCompare = Comparator.comparing(Visit::getDate);
        monthVisits.sort(visitCompare);

        return monthVisits;
    }


    private List<VolunteerVisitCount> initVolunteerVisitCounts(List<User> volunteers) {
        List<VolunteerVisitCount> volunteerVisitCounts = new ArrayList<>();
        for (User v : volunteers) {
            volunteerVisitCounts.add(new VolunteerVisitCount(v));
        }

        return volunteerVisitCounts;
    }

    private void removeBlockedDates(int month, int year){
        blockedDatesRepository.loadByMonth(month, year).getDates().stream().forEach(blockedDatesRepository::delete);
    }

}
