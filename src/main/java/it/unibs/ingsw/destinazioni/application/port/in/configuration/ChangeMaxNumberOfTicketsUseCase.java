package it.unibs.ingsw.destinazioni.application.port.in.configuration;

/**
 * Interface per la gestione del numero massimo di biglietti per utente.
 */
/*@ public invariant getMaxNumberOfTickets() > 0;
  @*/
public interface ChangeMaxNumberOfTicketsUseCase {

  /**
   * Imposta il numero massimo di biglietti per utente.
   * 
   * @param maxNumberOfTickets il numero massimo di biglietti (deve essere positivo)
   * @throws IllegalArgumentException se il numero è <= 0
   */
  /*@ requires maxNumberOfTickets > 0;
    @ ensures getMaxNumberOfTickets() == maxNumberOfTickets;
    @ signals (IllegalArgumentException e) maxNumberOfTickets <= 0;
    @*/
  void setMaxNumberOfTickets(int maxNumberOfTickets);


  /**
   * Ottiene il numero massimo di biglietti per utente configurato nel sistema.
   * 
   * @return il numero massimo di biglietti per utente
   */
  /*@ ensures \result > 0;
    @ pure
    @*/
  int getMaxNumberOfTickets();
}
