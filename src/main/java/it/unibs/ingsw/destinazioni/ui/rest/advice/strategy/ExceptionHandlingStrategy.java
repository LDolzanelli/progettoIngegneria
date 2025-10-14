package it.unibs.ingsw.destinazioni.ui.rest.advice.strategy;

import org.springframework.http.ResponseEntity;

/**
 * Interfaccia Strategy per la gestione delle eccezioni specifiche di ogni caso d'uso.
 * Ogni caso d'uso può implementare la propria strategia di gestione degli errori.
 */
public interface ExceptionHandlingStrategy {

    /**
     * Gestisce l'eccezione specifica del caso d'uso e restituisce una risposta appropriata.
     * 
     * @param exception L'eccezione da gestire
     * @return ResponseEntity con il messaggio di errore localizzato
     */
    ResponseEntity<String> handleException(Exception exception);


    /**
     * Indica se questa strategia può gestire il tipo di eccezione dato.
     * 
     * @param exception L'eccezione da verificare
     * @return true se la strategia può gestire l'eccezione, false altrimenti
     */
    boolean canHandle(Exception exception);


    /**
     * Restituisce l'identificativo del caso d'uso gestito da questa strategia.
     * 
     * @return Il nome del caso d'uso (es. "booking", "user", "visit")
     */
    String getUseCaseType();
}
