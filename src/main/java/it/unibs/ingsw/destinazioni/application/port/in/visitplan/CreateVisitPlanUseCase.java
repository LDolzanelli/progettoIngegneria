package it.unibs.ingsw.destinazioni.application.port.in.visitplan;

import it.unibs.ingsw.destinazioni.application.exceptions.usecases.VisitPlanException;

/**
 * Interface per la creazione dei piani di visita.
 */
public interface CreateVisitPlanUseCase {

  /**
   * Controlla se è possibile creare un piano di visita per il prossimo mese.
   * Il piano può essere creato se:
   * - il piano per il mese successivo non è stato ancora creato
   * - la raccolta di disponibilità per il mese successivo è stata chiusa
   * 
   * @return true se il piano può essere creato, false altrimenti
   */
  /*@ ensures \result == true || \result == false;
    @ pure
    @*/
  public boolean canCreateVisitPlan();


  /**
   * Crea il piano di visita per il prossimo mese.
   * Assegna i volontari alle visite disponibili e aggiorna gli stati delle visite.
   * 
   * @throws VisitPlanException se non è possibile creare il piano di visita
   */
  /*@ requires canCreateVisitPlan();
    @ ensures (\exists VisitPlanQueryUseCase query; 
    @          (\forall Visit v; query.getVisitPlan(getMonth(), getYear()).contains(v);
    @           v.getVolunteer() != null || v.getVisitStatus() == VisitStatus.CANCELLED));
    @ signals (VisitPlanException e) !canCreateVisitPlan();
    @*/
  public void createVisitPlan();


  /**
   * Ottiene il mese del prossimo piano di visita.
   * 
   * @return il mese successivo a quello corrente
   */
  /*@ ensures \result >= 1 && \result <= 12;
    @ ensures \result == (\current_month % 12) + 1;
    @ pure
    @*/
  public int getMonth();


  /**
   * Ottiene l'anno corrente.
   * 
   * @return l'anno corrente
   */
  /*@ ensures \result > 0;
    @ ensures \result == \current_year;
    @ pure
    @*/
  public int getYear();
}
