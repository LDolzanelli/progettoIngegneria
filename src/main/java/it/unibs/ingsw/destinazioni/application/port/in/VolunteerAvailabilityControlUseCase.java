package it.unibs.ingsw.destinazioni.application.port.in;

/**
 * Interface per il controllo della disponibilità dei volontari.
 */
/*@ public invariant getMonthToEnable() == (getCurrentMonth() + 2) % 12 + 1;
  @ public invariant getMonthToDisable() == (getCurrentMonth() + 1) % 12 + 1;
  @*/
public interface VolunteerAvailabilityControlUseCase {

    /**
     * Verifica se è possibile abilitare la disponibilità dei volontari per il mese i+2.
     * 
     * @return true se la disponibilità può essere abilitata, false altrimenti
     */
    /*@ ensures \result == (!isVolunteerAvailabilityOpen(getCurrentMonth() + 1) &&
      @                   isVisitPlanCreated(getCurrentMonth() + 1) &&
      @                   !isVolunteerAvailabilityOpen(getCurrentMonth() + 2) &&
      @                   getCurrentDay() >= 16);
      @ pure
      @*/
    boolean canEnableAvailability();


    /**
     * Verifica se è possibile disabilitare la disponibilità dei volontari per il mese i+1.
     * 
     * @return true se la disponibilità può essere disabilitata, false altrimenti
     */
    /*@ ensures \result == (isVolunteerAvailabilityOpen(getCurrentMonth() + 1) &&
      @                   getCurrentDay() > 15);
      @ pure
      @*/
    boolean canDisableAvailability();


    /**
     * Abilita la disponibilità dei volontari per il mese i+2.
     * 
     * @throws IllegalStateException se non è possibile abilitare la disponibilità
     */
    /*@ requires canEnableAvailability();
      @ ensures isVolunteerAvailabilityOpen(getMonthToEnable());
      @ ensures areDefaultVisitDaysCreated(getMonthToEnable());
      @ signals (IllegalStateException e) !canEnableAvailability();
      @*/
    void enableAvailability();


    /**
     * Disabilita la disponibilità dei volontari per il mese i+1.
     * 
     * @throws IllegalStateException se non è possibile disabilitare la disponibilità
     */
    /*@ requires canDisableAvailability();
      @ ensures !isVolunteerAvailabilityOpen(getMonthToDisable());
      @ signals (IllegalStateException e) !canDisableAvailability();
      @*/
    void disableAvailability();


    /**
     * Ottiene il numero del mese per cui è possibile abilitare la disponibilità.
     * 
     * @return il numero del mese (1-12) per cui abilitare la disponibilità
     */
    /*@ ensures 1 <= \result && \result <= 12;
      @ ensures \result == (getCurrentMonth() + 2) % 12 == 0 ? 12 : (getCurrentMonth() + 2) % 12;
      @ pure
      @*/
    int getMonthToEnable();


    /**
     * Ottiene il numero del mese per cui è possibile disabilitare la disponibilità.
     * 
     * @return il numero del mese (1-12) per cui disabilitare la disponibilità
     */
    /*@ ensures 1 <= \result && \result <= 12;
      @ ensures \result == (getCurrentMonth() + 1) % 12 == 0 ? 12 : (getCurrentMonth() + 1) % 12;
      @ pure
      @*/
    int getMonthToDisable();


    /**
     * Verifica se la disponibilità dei volontari è attualmente abilitata.
     * 
     * @return true se la disponibilità è abilitata, false altrimenti
     */
    /*@ ensures \result == (getCurrentDay() >= 16 ? 
      @                   isVolunteerAvailabilityOpen(getCurrentMonth() + 2) :
      @                   isVolunteerAvailabilityOpen(getCurrentMonth() + 1));
      @ pure
      @*/
    boolean isAvailabilityEnabled();
}
