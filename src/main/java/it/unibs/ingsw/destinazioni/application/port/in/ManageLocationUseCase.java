package it.unibs.ingsw.destinazioni.application.port.in;

import java.util.List;
import java.util.Optional;

import it.unibs.ingsw.destinazioni.domain.model.Location;
import it.unibs.ingsw.destinazioni.domain.model.VisitType;

/*
 * @
 * 
 * @ public invariant (\forall Location loc; listAll().contains(loc);
 * 
 * @ loc.getId() != null && loc.getName() != null && !loc.getName().trim().isEmpty());
 * 
 * @ public invariant (\forall Location loc; findById(loc.getId()).isPresent();
 * 
 * @ findById(loc.getId()).get().equals(loc));
 * 
 * @
 */
public interface ManageLocationUseCase {

    /**
     * Aggiunge una nuova location al sistema.
     * 
     * @param location la location da aggiungere
     * @throws IllegalArgumentException se la location è null o ha dati non validi
     */
    /*@ requires location != null;
      @ requires location.getName() != null && !location.getName().trim().isEmpty();
      @ requires location.getAddress() != null;
      @ ensures listAll().contains(location) || 
      @         (\exists Location saved; listAll().contains(saved);
      @          saved.getName().equals(location.getName()) && saved.getAddress().equals(location.getAddress()));
      @ signals (IllegalArgumentException e) location == null || 
      @                                      location.getName() == null || 
      @                                      location.getName().trim().isEmpty();
      @*/
    void addLocation(Location location);


    /**
     * Rimuove una location dal sistema.
     * 
     * @param locationId l'ID della location da rimuovere
     * @throws IllegalArgumentException se la location non esiste o non può essere rimossa
     */
    /*@ requires locationId > 0;
      @ requires findById(locationId).isPresent();
      @ requires canBeRemoved(locationId);
      @ ensures !findById(locationId).isPresent();
      @ signals (IllegalArgumentException e) !findById(locationId).isPresent() || 
      @                                      !canBeRemoved(locationId);
      @*/
    void removeLocation(int locationId);


    /**
     * Verifica se una location può essere rimossa.
     * 
     * @param locationId l'ID della location da verificare
     * @return true se può essere rimossa, false altrimenti
     * @throws IllegalArgumentException se la location non esiste
     */
    /*@ requires locationId > 0;
      @ requires findById(locationId).isPresent();
      @ ensures \result == (findById(locationId).get().getVisitTypes() == null ||
      @                    findById(locationId).get().getVisitTypes().isEmpty() ||
      @                    (\forall VisitType vt; findById(locationId).get().getVisitTypes().contains(vt);
      @                     canVisitTypeBeRemoved(vt.getId())));
      @ signals (IllegalArgumentException e) !findById(locationId).isPresent();
      @ pure
      @*/
    boolean canBeRemoved(int locationId);


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


    /**
     * Aggiorna una location esistente.
     * 
     * @param location la location con i dati aggiornati
     * @throws IllegalArgumentException se la location è null, l'ID è null o la location non esiste
     */
    /*@ requires location != null && location.getId() != null;
      @ requires location.getName() != null && !location.getName().trim().isEmpty();
      @ requires findById(location.getId()).isPresent();
      @ ensures findById(location.getId()).get().getName().equals(location.getName());
      @ ensures findById(location.getId()).get().getDescription().equals(location.getDescription());
      @ signals (IllegalArgumentException e) location == null || 
      @                                      location.getId() == null ||
      @                                      !findById(location.getId()).isPresent();
      @*/
    void updateLocation(Location location);
}
