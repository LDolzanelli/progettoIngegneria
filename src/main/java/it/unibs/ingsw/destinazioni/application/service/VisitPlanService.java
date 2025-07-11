package it.unibs.ingsw.destinazioni.application.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Service;

import it.unibs.ingsw.destinazioni.application.port.in.VisitPlanUseCase;
import it.unibs.ingsw.destinazioni.application.port.out.VisitPlanStatePort;
import it.unibs.ingsw.destinazioni.application.port.out.VolunteerAvailabilityStatePort;
import it.unibs.ingsw.destinazioni.domain.model.Visit;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VisitPlanService implements VisitPlanUseCase {

    private final VisitPlanStatePort statePort;
    private final VolunteerAvailabilityStatePort availabilityStatePort;
    private final Clock clock;

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

        statePort.setVisitPlanCreated(nextMonthValue, nextYear, true);

        // TODO: implementare la logica per creare il piano di visita
    }

    @Override
    public List<Visit> getVisitPlan(int month, int year) {
        // TODO: implementare la logica per ottenere il piano di visita
        return List.of(); // restituisce una lista vuota per ora
    }

    @Override
    public int getMonth() {
        LocalDate today = LocalDate.now(clock);
        return YearMonth.from(today).plusMonths(1).getMonthValue();
    }
}
