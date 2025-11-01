package it.unibs.ingsw.destinazioni.application.services;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import it.unibs.ingsw.destinazioni.application.exceptions.codes.VolunteerAvailabilityErrorCode;
import it.unibs.ingsw.destinazioni.application.exceptions.usecases.VolunteerAvailabilityException;
import it.unibs.ingsw.destinazioni.application.port.in.visit.VisitDaysUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.volunteer.VolunteerAvailabilityControlUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.volunteer.VolunteersAvailabilityUseCase;
import it.unibs.ingsw.destinazioni.application.port.out.VisitPlanStatePort;
import it.unibs.ingsw.destinazioni.application.port.out.VolunteerAvailabilityStatePort;
import it.unibs.ingsw.destinazioni.application.port.out.VolunteerAvailableDateRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.model.VolunteerAvailableDate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VolunteersAvailabilityService
    implements VolunteerAvailabilityControlUseCase, VolunteersAvailabilityUseCase {

  private final VolunteerAvailabilityStatePort statePort;
  private final VisitPlanStatePort visitPlanStatePort;
  private final VisitDaysUseCase visitDaysUseCase;
  private final VolunteerAvailableDateRepositoryPort repository;
  private final Clock clock;

  /*
   * Controlla se è possibile abilitare la disponibilità dei volontari per il mese
   * i+2.
   * Con i = mese corrente, i vincoli sono:
   * 1. La disponibilità dei volontari deve essere chiusa per il mese i+1.
   * 2. Il piano di visita per il mese i+1 deve essere stato creato.
   * 3. Il giorno corrente deve essere compreso tra il 16 del mese i e il 15 del
   * mese i+1.
   */
  @Override
  public boolean canEnableAvailability() {
    LocalDate today = LocalDate.now(clock);
    int todayDateNumber = today.getDayOfMonth();
    YearMonth currentYearMonth = YearMonth.from(today);
    YearMonth nextYearMonth = currentYearMonth.plusMonths(1);
    YearMonth nextPlusOneYearMonth = currentYearMonth.plusMonths(2);

    boolean nextMonthAvailabilityNotOpen = !statePort.isVolunteerAvailabilityOpen(nextYearMonth.getMonthValue(),
        nextYearMonth.getYear());
    boolean nextMonthVisitPlanCreated = visitPlanStatePort.isVisitPlanCreated(nextYearMonth.getMonthValue(),
        nextYearMonth.getYear());
    boolean nextTwoMonthsAvailabilityNotOpen = !statePort
        .isVolunteerAvailabilityOpen(nextPlusOneYearMonth.getMonthValue(), nextPlusOneYearMonth.getYear());
    boolean isTodayAfterSixteen = todayDateNumber >= 16;

    return nextMonthAvailabilityNotOpen && nextMonthVisitPlanCreated && nextTwoMonthsAvailabilityNotOpen
        && isTodayAfterSixteen;
  }

  /*
   * Controlla se è possibile disabilitare la disponibilità dei volontari per il
   * mese i+1.
   * Con i = mese corrente, i vincoli sono:
   * 1. La disponibilità dei volontari deve essere aperta per il mese i+1.
   * 2. Il giorno corrente deve essere dopo il 15 del mese i.
   */
  @Override
  public boolean canDisableAvailability() {
    LocalDate today = LocalDate.now(clock);
    int todayDateNumber = today.getDayOfMonth();
    YearMonth nextYearMonth = YearMonth.from(today).plusMonths(1);

    return statePort.isVolunteerAvailabilityOpen(nextYearMonth.getMonthValue(), nextYearMonth.getYear())
        && todayDateNumber > 15;
  }

  @Override
  public void enableAvailability() {
    YearMonth targetYearMonth = YearMonth.now(clock).plusMonths(2);

    if (!canEnableAvailability()) {

      throw new VolunteerAvailabilityException(VolunteerAvailabilityErrorCode.CANT_BE_ENABLED,
          "Non è possibile abilitare la disponibilità dei volontari per il mese "
              + targetYearMonth.getMonthValue() + "/" + targetYearMonth.getYear());

    }

    statePort.setVolunteerAvailabilityOpen(targetYearMonth.getMonthValue(), targetYearMonth.getYear(), true);
    visitDaysUseCase.createDefaultVisitDays(targetYearMonth.getMonthValue());
  }

  @Override
  public void disableAvailability() {
    YearMonth targetYearMonth = YearMonth.now(clock).plusMonths(1);

    if (!canDisableAvailability()) {
      throw new VolunteerAvailabilityException(VolunteerAvailabilityErrorCode.CANT_BE_DISABLED,
          "Non è possibile disabilitare la disponibilità dei volontari per il mese " + targetYearMonth.getMonthValue()
              + "/" + targetYearMonth.getYear());

    }
    statePort.setVolunteerAvailabilityOpen(targetYearMonth.getMonthValue(), targetYearMonth.getYear(), false);
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

    LocalDate today = LocalDate.now(clock);

    YearMonth nextYearMonth = today.getDayOfMonth() >= 16 ? YearMonth.from(today).plusMonths(2)
        : YearMonth.from(today).plusMonths(1);

    return statePort.isVolunteerAvailabilityOpen(nextYearMonth.getMonthValue(), nextYearMonth.getYear());
  }

  // ==== CRUD DISPONIBILITÀ VOLONTARI ====

  @Override
  public void updateAvailability(int volunteerId, Set<LocalDate> availableDates) {

    Month targetMonth = getTargetMonth();

    if (!isAvailabilityEnabled()) {
      throw new VolunteerAvailabilityException(VolunteerAvailabilityErrorCode.NOT_ENABLED,
          "La disponibilità dei volontari non è abilitata per il mese " + targetMonth.getValue());
    }

    repository.findByVolunteerId(volunteerId).stream()
        .filter(d -> d.getAvailableDate().getMonth() == targetMonth)
        .forEach(repository::delete);

    availableDates.forEach(d -> repository.save(new VolunteerAvailableDate(volunteerId, d)));
  }

  @Override
  public Set<LocalDate> getAvailability(int volunteerId) {
    return repository.findByVolunteerId(volunteerId).stream()
        .map(VolunteerAvailableDate::getAvailableDate)
        .collect(Collectors.toSet());
  }

  // gestisce anni diversi?
  // valutare se sostituire Month con un YearMonth
  @Override
  public Set<LocalDate> getAvailability(int volunteerId, Month month) {
    return repository.findByVolunteerId(volunteerId).stream()
        .filter(v -> v.getAvailableDate().getMonth() == month)
        .map(VolunteerAvailableDate::getAvailableDate)
        .collect(Collectors.toSet());
  }

  @Override
  public Month getTargetMonth() {
    LocalDate today = LocalDate.now(clock);
    int baseMonthValue = today.getDayOfMonth() < 16 ? today.getMonthValue() : today.plusMonths(1).getMonthValue();
    int targetMonthValue = (baseMonthValue % 12) + 1;
    return Month.of(targetMonthValue);
  }
}
