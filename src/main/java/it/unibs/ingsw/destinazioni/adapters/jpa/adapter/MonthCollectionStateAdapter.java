package it.unibs.ingsw.destinazioni.adapters.jpa.adapter;

import org.springframework.stereotype.Repository;
import it.unibs.ingsw.destinazioni.adapters.jpa.repository.MonthCollectionStateRepository;
import it.unibs.ingsw.destinazioni.application.port.out.VisitPlanStatePort;
import it.unibs.ingsw.destinazioni.application.port.out.VolunteerAvailabilityStatePort;

@Repository
public class MonthCollectionStateAdapter implements VolunteerAvailabilityStatePort, VisitPlanStatePort {

    private final MonthCollectionStateRepository monthCollectionStateRepository;

    public MonthCollectionStateAdapter(MonthCollectionStateRepository monthCollectionStateRepository) {
        this.monthCollectionStateRepository = monthCollectionStateRepository;
    }


    @Override
    public boolean isVolunteerAvailabilityOpen(int month, int year) {
        if (monthCollectionStateRepository.existsByMonthAndYear(month, year)) {
            return monthCollectionStateRepository.findByMonthAndYear(month, year)
                    .isVolunteersAvailabilityCollectionEnabled();
        } else {
            // se non esiste il mese e l'anno, allora assumo che non sia aperta la raccolta delle disponibilità
            return false;
        }
    }


    @Override
    public void setVolunteerAvailabilityOpen(int month, int year, boolean enabled) {
        if(monthCollectionStateRepository.existsByMonthAndYear(month, year)) {
            var monthCollectionState = monthCollectionStateRepository.findByMonthAndYear(month, year);
            monthCollectionState.setVolunteersAvailabilityCollectionEnabled(enabled);
            monthCollectionStateRepository.save(monthCollectionState);
        } else {
            //se non esiste il mese e l'anno, allora creo una nuova entry
            var newMonthCollectionState = new it.unibs.ingsw.destinazioni.adapters.jpa.entity.MonthCollectionStateEntity();
            newMonthCollectionState.setMonth(month);
            newMonthCollectionState.setYear(year);
            newMonthCollectionState.setVolunteersAvailabilityCollectionEnabled(enabled);
            monthCollectionStateRepository.save(newMonthCollectionState);
        }
    }


    @Override
    public boolean isVisitPlanCreated(int month, int year) {
        if (monthCollectionStateRepository.existsByMonthAndYear(month, year)) {
            return monthCollectionStateRepository.findByMonthAndYear(month, year)
                    .isVisitPlanCreated();
        } else {
            //se non esiste il mese e l'anno, allora assumo che sia stato creato il piano di visita
            return true;
        }
    }


    @Override
    public void setVisitPlanCreated(int month, int year, boolean created) {
        if(monthCollectionStateRepository.existsByMonthAndYear(month, year)) {
            var monthCollectionState = monthCollectionStateRepository.findByMonthAndYear(month, year);
            monthCollectionState.setVisitPlanCreated(created);
            monthCollectionStateRepository.save(monthCollectionState);
        } else {
            //se non esiste il mese e l'anno, allora creo una nuova entry
            var newMonthCollectionState = new it.unibs.ingsw.destinazioni.adapters.jpa.entity.MonthCollectionStateEntity();
            newMonthCollectionState.setMonth(month);
            newMonthCollectionState.setYear(year);
            newMonthCollectionState.setVisitPlanCreated(created);
            monthCollectionStateRepository.save(newMonthCollectionState);
        }
    }



}
