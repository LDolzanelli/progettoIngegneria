package it.unibs.ingsw.destinazioni.adapters.jpa.adapter;

import java.time.LocalDate;
import java.time.YearMonth;
import org.springframework.stereotype.Repository;
import it.unibs.ingsw.destinazioni.adapters.jpa.repository.MonthCollectionStateRepository;
import it.unibs.ingsw.destinazioni.application.port.out.VisitPlanStatePort;
import it.unibs.ingsw.destinazioni.application.port.out.VolunteerAvailabilityStatePort;
import it.unibs.ingsw.destinazioni.adapters.jpa.entity.MonthCollectionStateEntity;
import lombok.RequiredArgsConstructor;
import java.time.Clock;

@Repository
@RequiredArgsConstructor
public class MonthCollectionStateAdapter implements VolunteerAvailabilityStatePort, VisitPlanStatePort {

    private final MonthCollectionStateRepository monthCollectionStateRepository;
    private final Clock clock;


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
        if (monthCollectionStateRepository.existsByMonthAndYear(month, year)) {
            var monthCollectionState = monthCollectionStateRepository.findByMonthAndYear(month, year);
            monthCollectionState.setVolunteersAvailabilityCollectionEnabled(enabled);
            monthCollectionStateRepository.save(monthCollectionState);
        } else {
            // se non esiste il mese e l'anno, allora creo una nuova entry
            var newMonthCollectionState =
                    new it.unibs.ingsw.destinazioni.adapters.jpa.entity.MonthCollectionStateEntity();
            newMonthCollectionState.setMonth(month);
            newMonthCollectionState.setYear(year);
            newMonthCollectionState.setVolunteersAvailabilityCollectionEnabled(enabled);
            monthCollectionStateRepository.save(newMonthCollectionState);
        }
    }


    @Override
    public boolean isVisitPlanCreated(int month, int year) {
        MonthCollectionStateEntity mcse = monthCollectionStateRepository.findByMonthAndYear(month, year);
        if (mcse != null) {
            return mcse.isVisitPlanCreated();
        }

        LocalDate today = LocalDate.now(clock);
        YearMonth target = YearMonth.of(year, month);
        YearMonth current = YearMonth.from(today);

        if (current.equals(target)) {
            // Siamo nel mese richiesto
            return true;
        }

        if (current.equals(target.minusMonths(1)) && today.getDayOfMonth() > 15) {
            // Siamo nel mese precedente e dopo il 1
            return true;
        }

        if (current.isBefore(target.minusMonths(1))) {
            // Almeno due mesi prima
            return false;
        }

        // Tutti gli altri casi => dopo il mese richiesto
        return true;
    }



    @Override
    public void setVisitPlanCreated(int month, int year, boolean created) {
        if (monthCollectionStateRepository.existsByMonthAndYear(month, year)) {
            var monthCollectionState = monthCollectionStateRepository.findByMonthAndYear(month, year);
            monthCollectionState.setVisitPlanCreated(created);
            monthCollectionStateRepository.save(monthCollectionState);
        } else {
            // se non esiste il mese e l'anno, allora creo una nuova entry
            var newMonthCollectionState =
                    new it.unibs.ingsw.destinazioni.adapters.jpa.entity.MonthCollectionStateEntity();
            newMonthCollectionState.setMonth(month);
            newMonthCollectionState.setYear(year);
            newMonthCollectionState.setVisitPlanCreated(created);
            monthCollectionStateRepository.save(newMonthCollectionState);
        }
    }



}
