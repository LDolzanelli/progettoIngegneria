package it.unibs.ingsw.destinazioni.application.port.in.volunteer;

import java.time.LocalDate;
import java.time.Month;
import java.util.Set;

/**
 * Interfaccia per la gestione della disponibilità dei volontari.
 */
/*@ public invariant (\forall int volunteerId; getAvailability(volunteerId) != null;
  @                  volunteerId > 0);
  @ public invariant (\forall int volunteerId, Month month; 
  @                  getAvailability(volunteerId, month) != null;
  @                  volunteerId > 0 && month != null);
  @*/
public interface VolunteersAvailabilityUseCase {

  /**
   * Aggiorna la disponibilità di un volontario per il mese target.
   * 
   * @param volunteerId ID del volontario
   * @param availableDates insieme delle date di disponibilità
   * @throws IllegalStateException se la disponibilità non è abilitata per il mese target
   */
  /*@ requires volunteerId > 0;
    @ requires availableDates != null;
    @ requires isAvailabilityEnabled();
    @ requires (\forall LocalDate date; availableDates.contains(date); 
    @          date != null && date.getMonth() == getTargetMonth());
    @ ensures (\forall LocalDate date; availableDates.contains(date);
    @         getAvailability(volunteerId, getTargetMonth()).contains(date));
    @ ensures getAvailability(volunteerId, getTargetMonth()).size() == availableDates.size();
    @ signals (IllegalStateException e) !isAvailabilityEnabled();
    @*/
  void updateAvailability(int volunteerId, Set<LocalDate> availableDates);


  /**
   * Ottiene tutte le date di disponibilità di un volontario.
   * 
   * @param volunteerId ID del volontario
   * @return insieme di tutte le date di disponibilità del volontario
   */
  /*@ requires volunteerId > 0;
    @ ensures \result != null;
    @ ensures (\forall LocalDate date; \result.contains(date); date != null);
    @ pure
    @*/
  Set<LocalDate> getAvailability(int volunteerId);


  /**
   * Ottiene le date di disponibilità di un volontario per un mese specifico.
   * 
   * @param volunteerId ID del volontario
   * @param month mese per cui ottenere la disponibilità
   * @return insieme delle date di disponibilità del volontario per il mese specificato
   */
  /*@ requires volunteerId > 0;
    @ requires month != null;
    @ ensures \result != null;
    @ ensures (\forall LocalDate date; \result.contains(date); 
    @         date != null && date.getMonth() == month);
    @ ensures \result.subsetOf(getAvailability(volunteerId));
    @ pure
    @*/
  Set<LocalDate> getAvailability(int volunteerId, Month month);


  /**
   * Ottiene il mese target per l'aggiornamento della disponibilità.
   * 
   * @return il mese target basato sulla data corrente
   */
  /*@ ensures \result != null;
    @ ensures \result == (getCurrentDay() < 16 ? 
    @                   Month.of(((getCurrentMonth() % 12) + 1)) :
    @                   Month.of(((getCurrentMonth() + 1) % 12) + 1));
    @ pure
    @*/
  Month getTargetMonth();
}
