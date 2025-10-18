package it.unibs.ingsw.destinazioni.application.exceptions.base;

/**
 * Classe base per tutte le eccezioni dei casi d'uso.
 * Fornisce un'interfaccia comune per il pattern Strategy.
 */
public abstract class BaseUseCaseException extends RuntimeException {

    protected BaseUseCaseException(String message) {
        super(message);
    }

    protected BaseUseCaseException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Restituisce il tipo di caso d'uso che ha generato l'eccezione.
     * Utilizzato per determinare quale strategia di gestione usare.
     */
    public abstract String getUseCaseType();

    /**
     * Restituisce il codice di errore specifico del caso d'uso.
     */
    public abstract Object getErrorCode();
}
