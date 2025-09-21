package it.unibs.ingsw.destinazioni.application.port.in.visittype;

/**
 * Interface per le operazioni di validazione sui tipi di visita nel sistema.
 */
public interface VisitTypeValidationUseCase {

    /**
     * Verifica se un tipo di visita può essere rimosso.
     * 
     * @param visitTypeId l'ID del tipo di visita
     * @return true se può essere rimosso, false altrimenti
     */
    /*@ requires visitTypeId > 0;
    @ requires (\exists VisitTypeQueryUseCase query; 
    @           query.findById(visitTypeId).isPresent());
    @ ensures \result ==> isAddOrRemovalStateActive();
    @ pure
    @*/
    boolean canBeRemoved(int visitTypeId);


    /**
     * Verifica se un tipo di visita può essere modificato.
     * 
     * @param visitTypeId l'ID del tipo di visita
     * @return true se può essere modificato, false altrimenti
     */
    /*@ requires visitTypeId > 0;
    @ requires (\exists VisitTypeQueryUseCase query; 
    @           query.findById(visitTypeId).isPresent());
    @ ensures \result ==> canBeRemoved(visitTypeId);
    @ pure
    @*/
    boolean canBeModified(int visitTypeId);


    /**
     * Verifica se il sistema è nello stato che permette aggiunta/rimozione di tipi di visita.
     * 
     * @return true se è possibile aggiungere/rimuovere tipi di visita
     */
    /*@ pure
    @*/
    boolean isAddOrRemovalStateActive();
}
