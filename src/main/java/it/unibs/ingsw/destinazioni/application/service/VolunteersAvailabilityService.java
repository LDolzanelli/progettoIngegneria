package it.unibs.ingsw.destinazioni.application.service;

import it.unibs.ingsw.destinazioni.application.port.in.VolunteerAvailabilityControlUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.VolunteersAvailabilityUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.VisitDaysUseCase;
import it.unibs.ingsw.destinazioni.application.port.out.VisitPlanStatePort;
import it.unibs.ingsw.destinazioni.application.port.out.VolunteerAvailabilityStatePort;
import it.unibs.ingsw.destinazioni.application.port.out.VolunteerAvailableDateRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.model.VolunteerAvailableDate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VolunteersAvailabilityService
        implements VolunteerAvailabilityControlUseCase, VolunteersAvailabilityUseCase {

    private final VolunteerAvailabilityStatePort statePort;
    private final VisitPlanStatePort visitPlanStatePort;
    private final VisitDaysUseCase visitDaysUseCase;
    private final VolunteerAvailableDateRepositoryPort repository;
    private final Clock clock;

    // ==== CONTROLLI DI STATO ====

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
                && today >= 16;
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
        visitDaysUseCase.createDefaultVisitDays(target.getMonthValue());
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


    @Override
    public boolean isAvailabilityEnabled() {

        LocalDate now = LocalDate.now(clock);

        YearMonth next =
                now.getDayOfMonth() >= 16 ? YearMonth.from(now).plusMonths(2) : YearMonth.from(now).plusMonths(1);

        return statePort.isVolunteerAvailabilityOpen(next.getMonthValue(), next.getYear());
    }

    // ==== CRUD DISPONIBILITÀ VOLONTARI ====


    @Override
    public void updateAvailability(int volunteerId, Set<LocalDate> availableDates) {


        Month target = getTargetMonth();


        if (!isAvailabilityEnabled()) {
            throw new IllegalStateException("La disponibilità dei volontari non è abilitata per il mese " + target);
        }

        repository.findByVolunteerId(volunteerId).stream().filter(d -> d.getAvailableDate().getMonth() == target)
                .forEach(repository::delete);

        availableDates.forEach(d -> repository.save(new VolunteerAvailableDate(volunteerId, d)));
    }


    @Override
    public Set<LocalDate> getAvailability(int volunteerId) {
        return repository.findByVolunteerId(volunteerId).stream().map(VolunteerAvailableDate::getAvailableDate)
                .collect(Collectors.toSet());
    }

    // gestisce anni diversi?
    // valutare se sostituire Month con un YearMonth
    @Override
    public Set<LocalDate> getAvailability(int volunteerId, Month month) {
        return repository.findByVolunteerId(volunteerId).stream().filter(v -> v.getAvailableDate().getMonth() == month)
                .map(VolunteerAvailableDate::getAvailableDate).collect(Collectors.toSet());
    }


    @Override
    public Month getTargetMonth() {
        LocalDate today = LocalDate.now(clock);
        int base = today.getDayOfMonth() < 16 ? today.getMonthValue() : today.plusMonths(1).getMonthValue();
        int target = (base % 12) + 1;
        return Month.of(target);
    }
}
