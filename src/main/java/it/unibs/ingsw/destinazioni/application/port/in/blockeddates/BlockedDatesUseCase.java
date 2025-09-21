package it.unibs.ingsw.destinazioni.application.port.in.blockeddates;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Set;

import it.unibs.ingsw.destinazioni.domain.model.BlockedDates;

/**
 * Interface per la gestione delle date precluse nel sistema.
 */
/*@ public invariant (\forall LocalDate date; getBlockedDates().getDates().contains(date);
  @                   date != null);
  @ public invariant getBlockedDates() != null;
  @ public invariant getBlockedDates().getDates() != null;
  @*/
public interface BlockedDatesUseCase {

  /**
   * Aggiorna le date precluse.
   * 
   * @param blockedDates l'insieme delle date da inserire
   */
  /*@ requires blockedDates != null;
    @ ensures (\forall LocalDate date; blockedDates.contains(date); 
    @          getBlockedDates().getDates().contains(date));
    @*/
  void updateBlockedDates(Set<LocalDate> blockedDates);


  /**
   * Restituisce tutte le date precluse del sistema.
   * 
   * @return l'oggetto BlockedDates contenente tutte le date precluse
   */
  /*@ ensures \result != null;
    @ ensures \result.getDates() != null;
    @ pure
    @*/
  BlockedDates getBlockedDates();


  /**
   * Restituisce le date precluse per uno specifico mese.
   * 
   * @param month il mese di cui si vogliono ottenere le date precluse (1-12)
   * @param year  l'anno di cui si vogliono ottenere le date precluse
   * @return l'oggetto BlockedDates contenente tutte le date precluse del sistema
   * @throws IllegalArgumentException se il mese non è compreso tra 1 e 12
   */
  /*@ requires month >= 1 && month <= 12;
    @ ensures \result != null;
    @ ensures \result.getDates() != null;
    @ signals (IllegalArgumentException e) month < 1 || month > 12;
    @ pure
    @*/
  BlockedDates getBlockedDates(int month, int year);


  /**
   * Restituisce il mese che può essere aggiornato secondo la logica di business.
   * Il mese restituito dipende dalla data corrente e dalle regole di sistema.
   * 
   * @return il mese che può essere aggiornato
   */
  /*@ ensures \result != null;
    @ ensures \result.getMonth() != null;
    @ ensures \result.getYear() != null;
    @*/
  YearMonth getMonthToUpdate();
}
