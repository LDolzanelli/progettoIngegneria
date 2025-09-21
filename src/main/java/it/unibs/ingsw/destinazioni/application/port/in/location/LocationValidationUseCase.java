package it.unibs.ingsw.destinazioni.application.port.in.location;

/**
 * Interface per le operazioni di validazione sulle location nel sistema.
 */
public interface LocationValidationUseCase {

  /**
   * Verifica se una location può essere rimossa.
   * 
   * @param locationId l'ID della location da verificare
   * @return true se può essere rimossa, false altrimenti
   * @throws IllegalArgumentException se la location non esiste
   */
  /*@ requires locationId > 0;
    @ requires (\exists LocationQueryUseCase query; 
    @           query.findById(locationId).isPresent());
    @ ensures \result == (\exists LocationQueryUseCase query; 
    @                    query.findById(locationId).get().getVisitTypes() == null ||
    @                    query.findById(locationId).get().getVisitTypes().isEmpty() ||
    @                    (\forall VisitType vt; query.findById(locationId).get().getVisitTypes().contains(vt);
    @                     (\exists VisitTypeValidationUseCase validation; validation.canBeRemoved(vt.getId()))));
    @ signals (IllegalArgumentException e) !(\exists LocationQueryUseCase query; 
    @                                        query.findById(locationId).isPresent());
    @ pure
    @*/
  boolean canBeRemoved(int locationId);
}
