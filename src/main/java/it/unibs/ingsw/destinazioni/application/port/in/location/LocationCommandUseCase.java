package it.unibs.ingsw.destinazioni.application.port.in.location;

import it.unibs.ingsw.destinazioni.domain.model.Location;

/**
 * Interface per le operazioni di comando sulle location nel sistema.
 */
public interface LocationCommandUseCase {

    /**
     * Aggiunge una nuova location al sistema.
     * 
     * @param location la location da aggiungere
     * @throws IllegalArgumentException se la location è null o ha dati non validi
     */
    /*@ requires location != null;
    @ requires location.getName() != null && !location.getName().trim().isEmpty();
    @ requires location.getAddress() != null;
    @ ensures (\exists LocationQueryUseCase query; 
    @          query.listAll().stream().anyMatch(loc -> 
    @          loc.getName().equals(location.getName()) && 
    @          loc.getAddress().equals(location.getAddress())));
    @ signals (IllegalArgumentException e) location == null || 
    @                                      location.getName() == null || 
    @                                      location.getName().trim().isEmpty();
    @*/
    void addLocation(Location location);


    /**
     * Aggiorna una location esistente.
     * 
     * @param location la location con i dati aggiornati
     * @throws IllegalArgumentException se la location è null, l'ID è null o la location non esiste
     */
    /*@ requires location != null && location.getId() != null;
    @ requires location.getName() != null && !location.getName().trim().isEmpty();
    @ requires (\exists LocationQueryUseCase query; 
    @           query.findById(location.getId()).isPresent());
    @ ensures (\exists LocationQueryUseCase query; 
    @          query.findById(location.getId()).get().getName().equals(location.getName()) &&
    @          query.findById(location.getId()).get().getDescription().equals(location.getDescription()));
    @ signals (IllegalArgumentException e) location == null || 
    @                                      location.getId() == null ||
    @                                      !(\exists LocationQueryUseCase query; 
    @                                        query.findById(location.getId()).isPresent());
    @*/
    void updateLocation(Location location);


    /**
     * Rimuove una location dal sistema.
     * 
     * @param locationId l'ID della location da rimuovere
     * @throws IllegalArgumentException se la location non esiste o non può essere rimossa
     */
    /*@ requires locationId > 0;
    @ requires (\exists LocationQueryUseCase query; 
    @           query.findById(locationId).isPresent());
    @ requires (\exists LocationValidationUseCase validation; 
    @           validation.canBeRemoved(locationId));
    @ ensures (\exists LocationQueryUseCase query; 
    @          !query.findById(locationId).isPresent());
    @ signals (IllegalArgumentException e) !(\exists LocationQueryUseCase query; 
    @                                        query.findById(locationId).isPresent()) || 
    @                                      !(\exists LocationValidationUseCase validation; 
    @                                        validation.canBeRemoved(locationId));
    @*/
    void removeLocation(int locationId);
}
