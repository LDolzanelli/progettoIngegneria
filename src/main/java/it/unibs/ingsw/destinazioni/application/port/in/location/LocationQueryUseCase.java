package it.unibs.ingsw.destinazioni.application.port.in.location;

import java.util.List;
import java.util.Optional;

import it.unibs.ingsw.destinazioni.domain.model.Location;
import it.unibs.ingsw.destinazioni.domain.model.VisitType;

/**
 * Interface per le operazioni di query sulle location nel sistema.
 */
/*@ public invariant (\forall Location loc; listAll().contains(loc);
  @                   loc.getId() != null && loc.getName() != null && !loc.getName().trim().isEmpty());
  @ public invariant (\forall Location loc; findById(loc.getId()).isPresent();
  @                   findById(loc.getId()).get().equals(loc));
  @*/
public interface LocationQueryUseCase {

  /**
   * Restituisce la lista di tutte le location.
   * 
   * @return lista di tutte le location
   */
  /*@ ensures \result != null;
  @ ensures (\forall Location loc; \result.contains(loc);
  @          loc.getId() != null && loc.getName() != null && !loc.getName().trim().isEmpty());
  @ pure
  @*/
  List<Location> listAll();


  /**
   * Trova una location tramite il suo ID.
   * 
   * @param id l'ID della location
   * @return Optional contenente la location se trovata
   */
  /*@ requires id > 0;
  @ ensures \result != null;
  @ ensures \result.isPresent() ==> \result.get().getId().equals(id);
  @ pure
  @*/
  Optional<Location> findById(int id);


  /**
   * Trova la location associata a un tipo di visita.
   * 
   * @param visitType il tipo di visita
   * @return la location associata
   * @throws IllegalArgumentException se non c'è una location per il tipo di visita
   */
  /*@ requires visitType != null && visitType.getId() != null;
  @ ensures \result != null;
  @ ensures \result.getVisitTypes().contains(visitType);
  @ signals (IllegalArgumentException e) visitType == null || 
  @                                      visitType.getId() == null ||
  @                                      !(\exists Location loc; listAll().contains(loc);
  @                                        loc.getVisitTypes().contains(visitType));
  @ pure
  @*/
  Location getLocationForVisitType(VisitType visitType);
}
