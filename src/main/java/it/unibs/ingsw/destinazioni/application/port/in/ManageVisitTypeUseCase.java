package it.unibs.ingsw.destinazioni.application.port.in;

import java.util.Optional;
import java.util.Set;

import it.unibs.ingsw.destinazioni.domain.model.VisitType;

/**
 * Interface per la gestione dei tipi di visita nel sistema.
 */
/*@ public invariant (\forall VisitType vt; listAll().contains(vt);
  @                   vt.getId() != null && vt.getTitle() != null && !vt.getTitle().trim().isEmpty());
  @ public invariant (\forall VisitType vt; findById(vt.getId()).isPresent();
  @                   findById(vt.getId()).get().equals(vt));
  @ public invariant (\forall VisitType vt; listAll().contains(vt);
  @                   vt.getStartDate() != null && vt.getEndDate() != null &&
  @                   !vt.getStartDate().isAfter(vt.getEndDate()));
  @*/
public interface ManageVisitTypeUseCase {

  /**
   * Aggiunge un nuovo tipo di visita associato a una location.
   * 
   * @param visitType il tipo di visita da aggiungere
   * @param locationId l'ID della location a cui associare il tipo di visita
   * @throws IllegalArgumentException se il visitType è null o la location non esiste
   */
  /*@ requires visitType != null;
    @ requires visitType.getTitle() != null && !visitType.getTitle().trim().isEmpty();
    @ requires visitType.getStartDate() != null && visitType.getEndDate() != null;
    @ requires !visitType.getStartDate().isAfter(visitType.getEndDate());
    @ requires locationId > 0;
    @ ensures listAll().contains(visitType) || 
    @         (\exists VisitType saved; listAll().contains(saved);
    @          saved.getTitle().equals(visitType.getTitle()) && 
    @          saved.getStartDate().equals(visitType.getStartDate()));
    @ ensures listByLocation(locationId).size() >= \old(listByLocation(locationId).size());
    @ signals (IllegalArgumentException e) visitType == null || 
    @                                      visitType.getTitle() == null ||
    @                                      locationId <= 0;
    @*/
  void addVisitType(VisitType visitType, int locationId);


  /**
   * Rimuove un tipo di visita dal sistema.
   * 
   * @param visitTypeId l'ID del tipo di visita da rimuovere
   * @throws IllegalArgumentException se il tipo di visita non esiste o non può essere rimosso
   */
  /*@ requires visitTypeId > 0;
    @ requires findById(visitTypeId).isPresent();
    @ requires canBeRemoved(visitTypeId);
    @ requires isAddOrRemovalStateActive();
    @ ensures !findById(visitTypeId).isPresent();
    @ signals (IllegalArgumentException e) !findById(visitTypeId).isPresent() || 
    @                                      !canBeRemoved(visitTypeId) ||
    @                                      !isAddOrRemovalStateActive();
    @*/
  void removeVisitType(int visitTypeId);


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
   * Aggiorna un tipo di visita esistente.
   * 
   * @param visitType il tipo di visita con i dati aggiornati
   * @throws IllegalArgumentException se il visitType è null, l'ID è null o non esiste
   */
  /*@ requires visitType != null && visitType.getId() != null;
    @ requires visitType.getTitle() != null && !visitType.getTitle().trim().isEmpty();
    @ requires findById(visitType.getId()).isPresent();
    @ requires canBeModified(visitType.getId());
    @ ensures findById(visitType.getId()).get().getTitle().equals(visitType.getTitle());
    @ ensures findById(visitType.getId()).get().getDescription().equals(visitType.getDescription());
    @ signals (IllegalArgumentException e) visitType == null || 
    @                                      visitType.getId() == null ||
    @                                      !findById(visitType.getId()).isPresent() ||
    @                                      !canBeModified(visitType.getId());
    @*/
  void updateVisitType(VisitType visitType);


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


  /**
   * Verifica se un tipo di visita può essere rimosso.
   * 
   * @param visitTypeId l'ID del tipo di visita
   * @return true se può essere rimosso, false altrimenti
   */
  /*@ requires visitTypeId > 0;
    @ requires findById(visitTypeId).isPresent();
    @ ensures \result ==> isAddOrRemovalStateActive();
    @ pure
    @*/
  boolean canBeRemoved(int visitTypeId);


  /**
   * Verifica se il sistema è nello stato che permette aggiunta/rimozione di tipi di visita.
   * 
   * @return true se è possibile aggiungere/rimuovere tipi di visita
   */
  /*@ pure
    @*/
  boolean isAddOrRemovalStateActive();


  /**
   * Aggiunge un volontario a un tipo di visita.
   * 
   * @param visitTypeId l'ID del tipo di visita
   * @param nickname il nickname del volontario
   * @throws IllegalArgumentException se il tipo di visita non può essere modificato,
   *                                 il volontario non esiste, o è già associato
   */
  /*@ requires visitTypeId > 0 && nickname != null && !nickname.trim().isEmpty();
    @ requires findById(visitTypeId).isPresent();
    @ requires canBeModified(visitTypeId);
    @ ensures findById(visitTypeId).get().getVolunteers().stream()
    @         .anyMatch(v -> v.getNickname().equals(nickname));
    @ signals (IllegalArgumentException e) !findById(visitTypeId).isPresent() ||
    @                                      !canBeModified(visitTypeId) ||
    @                                      findById(visitTypeId).get().getVolunteers().stream()
    @                                      .anyMatch(v -> v.getNickname().equals(nickname));
    @*/
  void addVolunteerToVisitType(int visitTypeId, String nickname);


  /**
   * Verifica se un tipo di visita può essere modificato.
   * 
   * @param visitTypeId l'ID del tipo di visita
   * @return true se può essere modificato, false altrimenti
   */
  /*@ requires visitTypeId > 0;
    @ requires findById(visitTypeId).isPresent();
    @ ensures \result ==> canBeRemoved(visitTypeId);
    @ pure
    @*/
  boolean canBeModified(int visitTypeId);

}
