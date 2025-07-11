package it.unibs.ingsw.destinazioni.application.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;

import it.unibs.ingsw.destinazioni.application.port.in.VolunteerAvailabilityControlUseCase;
import it.unibs.ingsw.destinazioni.application.port.out.VisitPlanStatePort;
import it.unibs.ingsw.destinazioni.application.port.out.VolunteerAvailabilityStatePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VolunteerAvailabilityService implements VolunteerAvailabilityControlUseCase {

    private final VolunteerAvailabilityStatePort statePort;
    private final VisitPlanStatePort visitPlanStatePort;
    private final Clock clock;

    /**
     * Controlla se è possibile abilitare la disponibilità dei volontari per il mese i+2.
     * Con i = mese corrente, i vincoli sono:
     * 1. La disponibilità dei volontari deve essere chiusa per il mese i+1.
     * 2. Il piano di visita per il mese i+1 deve essere stato creato.
     * 3. Il giorno corrente deve essere compreso tra il 16 del mese i e il 15 del mese i+1.
     */
    @Override
    public boolean canEnableAvailability() {
        LocalDate now = LocalDate.now(clock);
        int today = now.getDayOfMonth();
        YearMonth current = YearMonth.from(now);
        YearMonth next = current.plusMonths(1);
        YearMonth nextPlusOne = current.plusMonths(2);

        return !statePort.isVolunteerAvailabilityOpen(next.getMonthValue(), next.getYear())
                && visitPlanStatePort.isVisitPlanCreated(next.getMonthValue(), next.getYear())
                && !statePort.isVolunteerAvailabilityOpen(nextPlusOne.getMonthValue(), nextPlusOne.getYear())
                && (today >= 16 || today <= 15); // Potresti voler raffinare questa logica.
    }


    /**
     * Controlla se è possibile disabilitare la disponibilità dei volontari per il mese i+1.
     * Con i = mese corrente, i vincoli sono:
     * 1. La disponibilità dei volontari deve essere aperta per il mese i+1.
     * 2. Il giorno corrente deve essere dopo il 15 del mese i.
     */
    @Override
    public boolean canDisableAvailability() {
        LocalDate now = LocalDate.now(clock);
        int today = now.getDayOfMonth();
        YearMonth next = YearMonth.from(now).plusMonths(1);

        return statePort.isVolunteerAvailabilityOpen(next.getMonthValue(), next.getYear()) && today > 15;
    }


    @Override
    public void enableAvailability() {
        if (!canEnableAvailability()) {
            YearMonth target = YearMonth.now(clock).plusMonths(2);
            throw new IllegalStateException("Non è possibile abilitare la disponibilità dei volontari per il mese "
                    + target.getMonthValue() + "/" + target.getYear());
        }

        YearMonth target = YearMonth.now(clock).plusMonths(2);
        statePort.setVolunteerAvailabilityOpen(target.getMonthValue(), target.getYear(), true);

        //TODO:Creazione visit days con stato proposed

    }


    @Override
    public void disableAvailability() {
        if (!canDisableAvailability()) {
            YearMonth target = YearMonth.now(clock).plusMonths(1);
            throw new IllegalStateException("Non è possibile disabilitare la disponibilità dei volontari per il mese "
                    + target.getMonthValue() + "/" + target.getYear());
        }

        YearMonth target = YearMonth.now(clock).plusMonths(1);
        statePort.setVolunteerAvailabilityOpen(target.getMonthValue(), target.getYear(), false);

    }


    @Override
    public int getMonthToEnable() {
        return YearMonth.now(clock).plusMonths(2).getMonthValue();
    }


    @Override
    public int getMonthToDisable() {
        return YearMonth.now(clock).plusMonths(1).getMonthValue();
    }
}

