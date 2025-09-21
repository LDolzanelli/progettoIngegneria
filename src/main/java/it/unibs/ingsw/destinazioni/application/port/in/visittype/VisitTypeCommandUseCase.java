package it.unibs.ingsw.destinazioni.application.port.in.visittype;

import it.unibs.ingsw.destinazioni.domain.model.VisitType;

/**
 * Interface per le operazioni di comando sui tipi di visita nel sistema.
 */
public interface VisitTypeCommandUseCase {

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
    @ ensures (\exists VisitTypeQueryUseCase query; 
    @          query.listAll().stream().anyMatch(vt -> 
    @          vt.getTitle().equals(visitType.getTitle()) && 
    @          vt.getStartDate().equals(visitType.getStartDate())));
    @ ensures (\exists VisitTypeQueryUseCase query; 
    @          query.listByLocation(locationId).size() >= \old(query.listByLocation(locationId).size()));
    @ signals (IllegalArgumentException e) visitType == null || 
    @                                      visitType.getTitle() == null ||
    @                                      locationId <= 0;
    @*/
    void addVisitType(VisitType visitType, int locationId);


    /**
     * Aggiorna un tipo di visita esistente.
     * 
     * @param visitType il tipo di visita con i dati aggiornati
     * @throws IllegalArgumentException se il visitType è null, l'ID è null o non esiste
     */
    /*@ requires visitType != null && visitType.getId() != null;
    @ requires visitType.getTitle() != null && !visitType.getTitle().trim().isEmpty();
    @ requires (\exists VisitTypeQueryUseCase query; 
    @           query.findById(visitType.getId()).isPresent());
    @ requires (\exists VisitTypeValidationUseCase validation; 
    @           validation.canBeModified(visitType.getId()));
    @ ensures (\exists VisitTypeQueryUseCase query; 
    @          query.findById(visitType.getId()).get().getTitle().equals(visitType.getTitle()) &&
    @          query.findById(visitType.getId()).get().getDescription().equals(visitType.getDescription()));
    @ signals (IllegalArgumentException e) visitType == null || 
    @                                      visitType.getId() == null ||
    @                                      !(\exists VisitTypeQueryUseCase query; 
    @                                        query.findById(visitType.getId()).isPresent()) ||
    @                                      !(\exists VisitTypeValidationUseCase validation; 
    @                                        validation.canBeModified(visitType.getId()));
    @*/
    void updateVisitType(VisitType visitType);


    /**
     * Rimuove un tipo di visita dal sistema.
     * 
     * @param visitTypeId l'ID del tipo di visita da rimuovere
     * @throws IllegalArgumentException se il tipo di visita non esiste o non può essere rimosso
     */
    /*@ requires visitTypeId > 0;
    @ requires (\exists VisitTypeQueryUseCase query; 
    @           query.findById(visitTypeId).isPresent());
    @ requires (\exists VisitTypeValidationUseCase validation; 
    @           validation.canBeRemoved(visitTypeId));
    @ requires (\exists VisitTypeValidationUseCase validation; 
    @           validation.isAddOrRemovalStateActive());
    @ ensures (\exists VisitTypeQueryUseCase query; 
    @          !query.findById(visitTypeId).isPresent());
    @ signals (IllegalArgumentException e) !(\exists VisitTypeQueryUseCase query; 
    @                                        query.findById(visitTypeId).isPresent()) || 
    @                                      !(\exists VisitTypeValidationUseCase validation; 
    @                                        validation.canBeRemoved(visitTypeId)) ||
    @                                      !(\exists VisitTypeValidationUseCase validation; 
    @                                        validation.isAddOrRemovalStateActive());
    @*/
    void removeVisitType(int visitTypeId);
}
