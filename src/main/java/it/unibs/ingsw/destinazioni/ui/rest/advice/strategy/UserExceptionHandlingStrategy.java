package it.unibs.ingsw.destinazioni.ui.rest.advice.strategy;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import it.unibs.ingsw.destinazioni.application.exceptions.codes.UserErrorCode;
import it.unibs.ingsw.destinazioni.application.exceptions.specific.UserException;

/**
 * Strategia per la gestione delle eccezioni relative al caso d'uso degli utenti.
 */
@Component
public class UserExceptionHandlingStrategy implements ExceptionHandlingStrategy {

    private static final String USE_CASE_TYPE = "user";

    @Override
    public ResponseEntity<String> handleException(Exception exception) {
        if (!(exception instanceof UserException userException)) {
            throw new IllegalArgumentException("Questa strategia gestisce solo UserException");
        }

        String userMessage = translateErrorCode(userException.getErrorCode());
        HttpStatus status = determineHttpStatus(userException.getErrorCode());

        return ResponseEntity.status(status).body(userMessage);
    }


    @Override
    public boolean canHandle(Exception exception) {
        return exception instanceof UserException;
    }


    @Override
    public String getUseCaseType() {
        return USE_CASE_TYPE;
    }


    /**
     * Traduce il codice di errore in un messaggio comprensibile all'utente.
     */
    private String translateErrorCode(UserErrorCode errorCode) {
        return switch (errorCode) {
            case USER_NOT_FOUND -> "Utente non trovato";
            case USER_ALREADY_EXISTS -> "Esiste già un utente con questi dati";
            case INVALID_CREDENTIALS -> "Credenziali non valide";
            case UNAUTHORIZED_REQUEST -> "Richiesta non autorizzata";
        };
    }


    /**
     * Determina lo status HTTP appropriato in base al tipo di errore.
     */
    private HttpStatus determineHttpStatus(UserErrorCode errorCode) {
        return switch (errorCode) {
            case USER_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case UNAUTHORIZED_REQUEST -> HttpStatus.FORBIDDEN;
            default -> HttpStatus.BAD_REQUEST;
        };
    }
}
