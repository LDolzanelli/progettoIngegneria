package it.unibs.ingsw.destinazioni.application.port.in.visittype;

import java.util.Optional;
import java.util.Set;

import it.unibs.ingsw.destinazioni.domain.model.VisitType;

/**
 * Interface per le operazioni di query sui tipi di visita nel sistema.
 */
/*@ public invariant (\forall VisitType vt; listAll().contains(vt);
  @                   vt.getId() != null && vt.getTitle() != null && !vt.getTitle().trim().isEmpty());
  @ public invariant (\forall VisitType vt; findById(vt.getId()).isPresent();
  @                   findById(vt.getId()).get().equals(vt));
  @ public invariant (\forall VisitType vt; listAll().contains(vt);
  @                   vt.getStartDate() != null && vt.getEndDate() != null &&
  @                   !vt.getStartDate().isAfter(vt.getEndDate()));
  @*/
public interface VisitTypeQueryUseCase {

  /**
   * Restituisce tutti i tipi di visita.
   * 
   * @return set di tutti i tipi di visita
   */
  /*@ ensures \result != null;
  @ ensures (\forall VisitType vt; \result.contains(vt);
  @          vt.getId() != null && vt.getTitle() != null && !vt.getTitle().trim().isEmpty());
  @ pure
  @*/
  Set<VisitType> listAll();


  /**
   * Trova un tipo di visita tramite il suo ID.
   * 
   * @param id l'ID del tipo di visita
   * @return Optional contenente il tipo di visita se trovato
   */
  /*@ requires id > 0;
  @ ensures \result != null;
  @ ensures \result.isPresent() ==> \result.get().getId().equals(id);
  @ pure
  @*/
  Optional<VisitType> findById(int id);


  /**
   * Restituisce i tipi di visita associati a una location.
   * 
   * @param locationId l'ID della location
   * @return set dei tipi di visita della location
   */
  /*@ requires locationId > 0;
  @ ensures \result != null;
  @ ensures (\forall VisitType vt; \result.contains(vt); listAll().contains(vt));
  @ pure
  @*/
  Set<VisitType> listByLocation(int locationId);


  /**
   * Restituisce i tipi di visita associati a un volontario.
   * 
   * @param volunteerId l'ID del volontario
   * @return set dei tipi di visita del volontario
   */
  /*@ requires volunteerId > 0;
  @ ensures \result != null;
  @ ensures (\forall VisitType vt; \result.contains(vt); 
  @          vt.getVolunteers().stream().anyMatch(v -> v.getId().equals(volunteerId)));
  @ pure
  @*/
  Set<VisitType> listByVolunteerId(int volunteerId);
}
