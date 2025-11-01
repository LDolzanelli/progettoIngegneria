package it.unibs.ingsw.destinazioni.application.port.in.visittype;

import it.unibs.ingsw.destinazioni.application.exceptions.usecases.VisitTypeException;

/**
 * Interface per l'assegnazione di volontari ai tipi di visita nel sistema.
 */
public interface AssignVolunteerToVisitTypeUseCase {

    /**
     * Aggiunge un volontario a un tipo di visita.
     * 
     * @param visitTypeId l'ID del tipo di visita
     * @param nickname il nickname del volontario
     * @throws VisitTypeException se il tipo di visita non può essere modificato,
     *                                 il volontario non esiste, o è già associato
     */
    /*@ requires visitTypeId > 0 && nickname != null && !nickname.trim().isEmpty();
    @ requires (\exists VisitTypeQueryUseCase query; 
    @           query.findById(visitTypeId).isPresent());
    @ requires (\exists VisitTypeValidationUseCase validation; 
    @           validation.canBeModified(visitTypeId));
    @ ensures (\exists VisitTypeQueryUseCase query; 
    @          query.findById(visitTypeId).get().getVolunteers().stream()
    @          .anyMatch(v -> v.getNickname().equals(nickname)));
    @ signals (VisitTypeException e) !(\exists VisitTypeQueryUseCase query; 
    @                                        query.findById(visitTypeId).isPresent()) ||
    @                                      !(\exists VisitTypeValidationUseCase validation; 
    @                                        validation.canBeModified(visitTypeId)) ||
    @                                      (\exists VisitTypeQueryUseCase query; 
    @                                       query.findById(visitTypeId).get().getVolunteers().stream()
    @                                       .anyMatch(v -> v.getNickname().equals(nickname)));
    @*/
    void addVolunteerToVisitType(int visitTypeId, String nickname);
}
