package it.unibs.ingsw.destinazioni.application.services;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

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
   Controlla se è possibile abilitare la disponibilità dei volontari per il mese
   i+2.
   Con i = mese corrente, i vincoli sono:
   1. La disponibilità dei volontari deve essere chiusa per il mese i+1.
   2. Il piano di visita per il mese i+1 deve essere stato creato.
   3. Il giorno corrente deve essere compreso tra il 16 del mese i e il 15 del
   mese i+1.
   */
  @Override
  /*@ also
    @ requires statePort != null && visitPlanStatePort != null && clock != null;
    @ ensures \result == (!statePort.isVolunteerAvailabilityOpen(LocalDate.now(clock).plusMonths(1).getMonthValue(), 
    @                                                           LocalDate.now(clock).plusMonths(1).getYear()) &&
    @                    visitPlanStatePort.isVisitPlanCreated(LocalDate.now(clock).plusMonths(1).getMonthValue(),
    @                                                         LocalDate.now(clock).plusMonths(1).getYear()) &&
    @                    !statePort.isVolunteerAvailabilityOpen(LocalDate.now(clock).plusMonths(2).getMonthValue(),
    @                                                          LocalDate.now(clock).plusMonths(2).getYear()) &&
    @                    LocalDate.now(clock).getDayOfMonth() >= 16);
    @*/
  public boolean canEnableAvailability() {
    LocalDate now = LocalDate.now(clock);
    int today = now.getDayOfMonth();
    YearMonth current = YearMonth.from(now);
    YearMonth next = current.plusMonths(1);
    YearMonth nextPlusOne = current.plusMonths(2);

    boolean nextMonthAvailabilityNotOpen = !statePort.isVolunteerAvailabilityOpen(next.getMonthValue(), next.getYear());
    boolean nextMonthVisitPlanCreated = visitPlanStatePort.isVisitPlanCreated(next.getMonthValue(), next.getYear());
    boolean nextTwoMonthsAvailabilityNotOpen =
        !statePort.isVolunteerAvailabilityOpen(nextPlusOne.getMonthValue(), nextPlusOne.getYear());
    boolean isTodayAfterSixteen = today >= 16;

    return nextMonthAvailabilityNotOpen && nextMonthVisitPlanCreated && nextTwoMonthsAvailabilityNotOpen
        && isTodayAfterSixteen;
  }


  /*
    Controlla se è possibile disabilitare la disponibilità dei volontari per il
    mese i+1.
    Con i = mese corrente, i vincoli sono:
    1. La disponibilità dei volontari deve essere aperta per il mese i+1.
    2. Il giorno corrente deve essere dopo il 15 del mese i.
   */
  @Override
  /*@ also
    @ requires statePort != null && clock != null;
    @ ensures \result == (statePort.isVolunteerAvailabilityOpen(LocalDate.now(clock).plusMonths(1).getMonthValue(),
    @                                                          LocalDate.now(clock).plusMonths(1).getYear()) &&
    @                    LocalDate.now(clock).getDayOfMonth() > 15);
    @*/
  public boolean canDisableAvailability() {
    LocalDate now = LocalDate.now(clock);
    int today = now.getDayOfMonth();
    YearMonth next = YearMonth.from(now).plusMonths(1);

    return statePort.isVolunteerAvailabilityOpen(next.getMonthValue(), next.getYear()) && today > 15;
  }


  @Override
  /*@ also
    @ requires canEnableAvailability();
    @ ensures statePort.isVolunteerAvailabilityOpen(YearMonth.now(clock).plusMonths(2).getMonthValue(),
    @                                               YearMonth.now(clock).plusMonths(2).getYear());
    @ signals (IllegalStateException e) !canEnableAvailability();
    @*/
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
  /*@ also
    @ requires canDisableAvailability();
    @ ensures !statePort.isVolunteerAvailabilityOpen(YearMonth.now(clock).plusMonths(1).getMonthValue(),
    @                                                YearMonth.now(clock).plusMonths(1).getYear());
    @ signals (IllegalStateException e) !canDisableAvailability();
    @*/
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
  /*@ also
    @ ensures 1 <= \result && \result <= 12;
    @ ensures \result == YearMonth.now(clock).plusMonths(2).getMonthValue();
    @*/
  public int getMonthToEnable() {
    return YearMonth.now(clock).plusMonths(2).getMonthValue();
  }


  @Override
  /*@ also
    @ ensures 1 <= \result && \result <= 12;
    @ ensures \result == YearMonth.now(clock).plusMonths(1).getMonthValue();
    @*/
  public int getMonthToDisable() {
    return YearMonth.now(clock).plusMonths(1).getMonthValue();
  }


  @Override
  /*@ also
    @ ensures \result == (LocalDate.now(clock).getDayOfMonth() >= 16 ?
    @                    statePort.isVolunteerAvailabilityOpen(LocalDate.now(clock).plusMonths(2).getMonthValue(),
    @                                                         LocalDate.now(clock).plusMonths(2).getYear()) :
    @                    statePort.isVolunteerAvailabilityOpen(LocalDate.now(clock).plusMonths(1).getMonthValue(),
    @                                                         LocalDate.now(clock).plusMonths(1).getYear()));
    @*/
  public boolean isAvailabilityEnabled() {

    LocalDate now = LocalDate.now(clock);

    YearMonth next = now.getDayOfMonth() >= 16 ? YearMonth.from(now).plusMonths(2) : YearMonth.from(now).plusMonths(1);

    return statePort.isVolunteerAvailabilityOpen(next.getMonthValue(), next.getYear());
  }

  // ==== CRUD DISPONIBILITÀ VOLONTARI ====


  @Override
  /*@ also
    @ requires volunteerId > 0 && availableDates != null;
    @ requires isAvailabilityEnabled();
    @ ensures (\forall LocalDate date; availableDates.contains(date);
    @         getAvailability(volunteerId, getTargetMonth()).contains(date));
    @ signals (IllegalStateException e) !isAvailabilityEnabled();
    @*/
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
  /*@ also
    @ requires volunteerId > 0;
    @ ensures \result != null;
    @ ensures (\forall LocalDate date; \result.contains(date); date != null);
    @*/
  public Set<LocalDate> getAvailability(int volunteerId) {
    return repository.findByVolunteerId(volunteerId).stream().map(VolunteerAvailableDate::getAvailableDate)
        .collect(Collectors.toSet());
  }


  // gestisce anni diversi?
  // valutare se sostituire Month con un YearMonth
  @Override
  /*@ also
    @ requires volunteerId > 0 && month != null;
    @ ensures \result != null;
    @ ensures (\forall LocalDate date; \result.contains(date); 
    @         date != null && date.getMonth() == month);
    @*/
  public Set<LocalDate> getAvailability(int volunteerId, Month month) {
    return repository.findByVolunteerId(volunteerId).stream().filter(v -> v.getAvailableDate().getMonth() == month)
        .map(VolunteerAvailableDate::getAvailableDate).collect(Collectors.toSet());
  }


  @Override
  /*@ also
    @ ensures \result != null;
    @ ensures \result == Month.of(LocalDate.now(clock).getDayOfMonth() < 16 ?
    @                            ((LocalDate.now(clock).getMonthValue() % 12) + 1) :
    @                            (((LocalDate.now(clock).getMonthValue() + 1) % 12) + 1));
    @*/
  public Month getTargetMonth() {
    LocalDate today = LocalDate.now(clock);
    int base = today.getDayOfMonth() < 16 ? today.getMonthValue() : today.plusMonths(1).getMonthValue();
    int target = (base % 12) + 1;
    return Month.of(target);
  }
}
