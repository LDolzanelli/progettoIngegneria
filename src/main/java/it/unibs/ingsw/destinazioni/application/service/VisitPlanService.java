package it.unibs.ingsw.destinazioni.application.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Comparator;

import org.springframework.stereotype.Service;

import it.unibs.ingsw.destinazioni.application.port.in.VisitPlanUseCase;
import it.unibs.ingsw.destinazioni.application.port.out.VisitPlanStatePort;
import it.unibs.ingsw.destinazioni.application.port.out.VolunteerAvailabilityStatePort;
import it.unibs.ingsw.destinazioni.domain.model.Visit;
import it.unibs.ingsw.destinazioni.domain.model.enums.VisitStatus;
import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.model.VisitType;
import it.unibs.ingsw.destinazioni.domain.model.VolunteerAvailableDate;

import it.unibs.ingsw.destinazioni.application.port.out.VisitTypeRepositoryPort;
import it.unibs.ingsw.destinazioni.application.port.out.VolunteerAvailableDateRepositoryPort;
import it.unibs.ingsw.destinazioni.application.port.out.VisitRepositoryPort;

import it.unibs.ingsw.destinazioni.application.service.UserService;

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

    private final UserService userService;
    private final VolunteerAvailableDateRepositoryPort volunteerAvailabilityRepository;
    private final VisitRepositoryPort visitRepository;

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

        //per debug, da togliere
        System.out.println("Data: " + LocalDate.now(clock).toString());
        System.out.println("NextMonthValue: " + nextMonthValue + "\tNextYear: " + nextYear);
        System.out.println("VisitPlanCreated: " + statePort.isVisitPlanCreated(nextMonthValue, nextYear));
        System.out.println("VolunteerAvailabilityOpen: " + availabilityStatePort.isVolunteerAvailabilityOpen(nextMonthValue, nextYear));

        if (statePort.isVisitPlanCreated(nextMonthValue, nextYear))
            return false;
        if (availabilityStatePort.isVolunteerAvailabilityOpen(nextMonthValue, nextYear))
            return false;
        return true;
    }

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


        List<VisitType> visitTypes = new ArrayList<VisitType>();
        visitTypes.addAll( visitTypeRepository.findAll() );

        List<VolunteerAvailableDate>  monthAvailabilities = initMonthAvailabilities(volunteers, nextMonthValue, nextYear);

        // recupera le visite del prossimo mese e ordinale
        List<Visit> monthVisits = initMonthVisits(nextMonthValue, nextYear);

        List<VolunteerVisitCount> volunteerVisitCounts = new ArrayList<VolunteerVisitCount>();

        for(Visit visit : monthVisits){
            LocalDate day = visit.getDate();
            System.out.println("Visita: " +day);
            List<User> availableVolunteers = new ArrayList<User>();

            // aggiunge alla lista availableVolunteers tutti i volontari che hanno dato disponibilitá per il giorno 'day'
            monthAvailabilities
                    .stream()
                    .filter( av -> av.getAvailableDate().equals(day) )
                    .map(av -> volunteerById( av.getVolunteerId(), volunteers) ) // mappa le disponibilitá (VolunteerAvailableDate) con gli oggetti dei volontari (User di tipo VOLUNTEER)
                    .forEach(availableVolunteers::add);


            System.out.println("Volontari disponibili " + availableVolunteers.size());
            if(availableVolunteers.isEmpty()){
                visit.setVisitStatus(VisitStatus.CANCELLED);
                visitRepository.save(visit);
                continue; // passa alla visita successiva
            } else if(availableVolunteers.size()==1){
                User volunteer = availableVolunteers.getFirst();
                if(canDoVisitType(volunteer, visit.getVisitType()))
                    assignVolunteerToVisit(volunteer, visit, monthAvailabilities, volunteerVisitCounts);

                continue; // passa alla visita successiva
            }

            System.out.println("Visita con più volontari: " + availableVolunteers.size() + "\til giorno: " +day);
            User test = availableVolunteers.getFirst();
            System.out.println("Test: " + test.getId());
            // piú di 2 volontari con disponibilitá nel giorno considerato
            User bestVolunteer= selectBestVolunteer(availableVolunteers, volunteerVisitCounts);
            System.out.println("BestVolunteer: " + bestVolunteer.getId());
            assignVolunteerToVisit(bestVolunteer, visit, monthAvailabilities, volunteerVisitCounts);
        }
        // tutte le visite sono state esaminate

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

    // valutare se inserirla in una classe separata (viene utilizzata solo per la creazione del piano visite)
    @Getter
    @Setter
    private class VolunteerVisitCount{
        private User volunteer;
        private int visitCount=0;

        public VolunteerVisitCount(User volunteer) {
            this.volunteer = volunteer;
        }
        public void increment(){
            visitCount++;
        }
    }

    private int visitCountFromVolunteer(User volunteer, List<VolunteerVisitCount> volunteerVisitCounts){
        for(VolunteerVisitCount vvc : volunteerVisitCounts){
            if(vvc.getVolunteer().equals(volunteer))
                return vvc.getVisitCount();
        }

        return -1;
    }
    // usare questo o lo UserRepository
    private User volunteerById(int id, List<User> volunteers){
        Optional<User> volunteer = volunteers.stream().filter(v -> v.getId() == id).findAny();
        if(volunteer.isPresent())
             return volunteer.get();
        else throw new RuntimeException("Volunteer with id " + id + " not found in volunteer list!");
    }

    private boolean canDoVisitType(User volunteer, VisitType visitType) {
        Set<VisitType> visitTypes = visitTypeRepository.findByVolunteerId(volunteer.getId());
        if(visitTypes.contains(visitType) )
            return true;
        else return false;
    }

    // valutare se crearne una versione diversa o con policy a scelta
//    private User selectBestVolunteer(List<User> volunteers, List<VolunteerVisitCount> volunteerVisitCounts){
//        User bestVolunteer = volunteers.getFirst();  //PROBLEMA: dá errore quando si recupera un elemento della lista
//        for(User v : volunteers){
//            int visitCount = volunteerVisitCounts.stream().filter( vvc -> vvc.volunteer.equals(v)).findFirst().get().getVisitCount();
//
//            // cerca il punteggio del volontario migliore
//            for(VolunteerVisitCount vvc : volunteerVisitCounts){
//                // se il conteggio visite del volontario considerato é minore di quello migliore, diventa il migliore
//                if(vvc.getVolunteer().equals(bestVolunteer))
//                    if(visitCount < vvc.getVisitCount()) {
//                        bestVolunteer = v;
//                        break;
//                    }
//            }
//        }
//
//        return bestVolunteer;
//    }

    private User selectBestVolunteer(List<User> volunteers, List<VolunteerVisitCount> volunteerVisitCounts){

        Comparator<User> volunteerComparator =
                ( v1,  v2) -> Integer.compare(visitCountFromVolunteer(v1, volunteerVisitCounts), visitCountFromVolunteer(v2, volunteerVisitCounts));

        return volunteers.stream().min(volunteerComparator).get();
    }

    /*  Assegna un volontario a una visita
        1) imposta il volontario nel campo volunteer della visita
        2) mette la visita in stato PROPOSED
        3) incrementa il conteggio delle visite del volontario
        4) rimuove la disponibilitá utilizzata per coprire la visita
        5) salva la visita
     */
    private void assignVolunteerToVisit(User volunteer, Visit visit, List<VolunteerAvailableDate> volunteerAvailabilities, List<VolunteerVisitCount> volunteerVisitCounts){
        visit.setVolunteer(volunteer);
        visit.setVisitStatus(VisitStatus.PROPOSED);
        volunteerVisitCounts.stream().filter(vvc -> vvc.getVolunteer().getId() == volunteer.getId()).findFirst().get().increment();
        volunteerAvailabilities.stream().filter(av -> av.getVolunteerId() == volunteer.getId()).forEach(volunteerAvailabilities::remove);
        visitRepository.save(visit);
        printVisitDetails(visit);
    }

    private List<VolunteerAvailableDate> initMonthAvailabilities(List<User> volunteers, int month, int year){
        List<VolunteerAvailableDate> monthAvailabilities = new ArrayList<>();
        for(User v : volunteers){
            // per ogni volontario recupera le sue disponibilitá e seleziona quelle del mese selezionato
            List<VolunteerAvailableDate> volunteerAvailabilities = new ArrayList<>();

            volunteerAvailabilityRepository.findByVolunteerId(v.getId())
                    .stream()
                    .filter( va -> {return va.getAvailableDate().getMonthValue() == month && va.getAvailableDate().getYear() ==year; } )
                    .forEach(volunteerAvailabilities::add );

            if( !volunteerAvailabilities.isEmpty())
                monthAvailabilities.addAll(volunteerAvailabilities);
        }

        return monthAvailabilities;
    }

    private List<Visit> initMonthVisits(int month, int year){
        List<Visit> monthVisits = new ArrayList<Visit>();

        visitRepository.findAll()
                .stream()
                .filter(visit -> { return visit.getDate().getMonthValue() == month && visit.getDate().getYear() == year; } )
                .forEach(monthVisits::add);

        Comparator visitCompare = Comparator.comparing(Visit::getDate);
        monthVisits.sort(visitCompare);

        return monthVisits;
    }

    // solo per debug, rimuovere nelle prossime versioni
    private void printVisitDetails(Visit visit){
        System.out.println("Dettagli visita: ");
        System.out.println("Giorno: " + visit.getDate() + " di tipo: " +  visit.getVisitType().getId());
        System.out.println("Volontario: " + visit.getVolunteer().getId());
    }
}
