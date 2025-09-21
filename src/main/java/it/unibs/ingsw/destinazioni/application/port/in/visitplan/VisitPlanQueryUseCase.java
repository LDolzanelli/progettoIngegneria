package it.unibs.ingsw.destinazioni.application.port.in.visitplan;

import java.util.List;

import it.unibs.ingsw.destinazioni.domain.model.Visit;

/**
 * Interface per le operazioni di query sui piani di visita.
 */
/*@ 
  @ public invariant (\forall Visit v; getAllVisitsAfterToday().contains(v);
  @                  v.getDate() != null && v.getDate().isAfter(\today));
  @ public invariant (\forall Visit v; getAllCompletedVisits().contains(v);
  @                  v.getVisitStatus() == VisitStatus.COMPLETED);
  @*/
public interface VisitPlanQueryUseCase {

  /**
   * Ottiene il piano di visita per un mese e anno specificati.
   * 
   * @param month il mese del piano di visita
   * @param year l'anno del piano di visita
   * @return lista delle visite per il mese e anno specificati
   * @throws IllegalArgumentException se mese o anno non sono validi
   */
  /*@ requires month >= 1 && month <= 12;
    @ requires year > 0;
    @ ensures \result != null;
    @ ensures (\forall Visit v; \result.contains(v);
    @          v.getDate().getMonthValue() == month && v.getDate().getYear() == year);
    @ signals (IllegalArgumentException e) month < 1 || month > 12 || year <= 0;
    @ pure
    @*/
  public List<Visit> getVisitPlan(int month, int year);


  /**
   * Ottiene tutte le visite programmate dopo la data odierna.
   * 
   * @return lista delle visite future
   */
  /*@ ensures \result != null;
    @ ensures (\forall Visit v; \result.contains(v);
    @          v.getDate().isAfter(\today) && v.getVolunteer() != null &&
    @          v.getVisitStatus() != VisitStatus.COMPLETED);
    @ pure
    @*/
  public List<Visit> getAllVisitsAfterToday();


  /**
   * Ottiene tutte le visite completate.
   * 
   * @return lista delle visite completate
   */
  /*@ ensures \result != null;
    @ ensures (\forall Visit v; \result.contains(v);
    @          v.getVisitStatus() == VisitStatus.COMPLETED);
    @ pure
    @*/
  public List<Visit> getAllCompletedVisits();
}
