package it.unibs.ingsw.destinazioni.application.port.in;

import java.util.List;

import it.unibs.ingsw.destinazioni.domain.model.Visit;

/**
 * Interface per la gestione dei piani di visita.
 */
/*@ 
  @ public invariant (\forall Visit v; getAllVisitsAfterToday().contains(v);
  @                  v.getDate() != null && v.getDate().isAfter(\today));
  @ public invariant (\forall Visit v; getAllCompletedVisits().contains(v);
  @                  v.getVisitStatus() == VisitStatus.COMPLETED);
  @*/
public interface VisitPlanUseCase {

    /**
     * Controlla se è possibile creare un piano di visita per il prossimo mese.
     * Il piano può essere creato se:
     * - il piano per il mese successivo non è stato ancora creato
     * - la raccolta di disponibilità per il mese successivo è stata chiusa
     * 
     * @return true se il piano può essere creato, false altrimenti
     */
    /*@ ensures \result == (!isVisitPlanCreated(getMonth(), getYear()) &&
      @                    !isVolunteerAvailabilityOpen(getMonth(), getYear()));
      @ pure
      @*/
    public boolean canCreateVisitPlan();


    /**
     * Crea il piano di visita per il prossimo mese.
     * Assegna i volontari alle visite disponibili e aggiorna gli stati delle visite.
     * 
     * @throws IllegalStateException se non è possibile creare il piano di visita
     */
    /*@ requires canCreateVisitPlan();
      @ ensures isVisitPlanCreated(getMonth(), getYear());
      @ ensures (\forall Visit v; getVisitPlan(getMonth(), getYear()).contains(v);
      @          v.getVolunteer() != null || v.getVisitStatus() == VisitStatus.CANCELLED);
      @ signals (IllegalStateException e) !canCreateVisitPlan();
      @*/
    public void createVisitPlan();


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
