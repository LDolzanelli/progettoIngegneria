package it.unibs.ingsw.destinazioni.ui.rest.advice.strategy;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import it.unibs.ingsw.destinazioni.application.exceptions.codes.BookingErrorCode;
import it.unibs.ingsw.destinazioni.application.exceptions.specific.BookingException;

/**
 * Strategia per la gestione delle eccezioni relative al caso d'uso delle prenotazioni.
 */
@Component
public class BookingExceptionHandlingStrategy implements ExceptionHandlingStrategy {

    private static final String USE_CASE_TYPE = "booking";

    @Override
    public ResponseEntity<String> handleException(Exception exception) {
        if (!(exception instanceof BookingException bookingException)) {
            throw new IllegalArgumentException("Questa strategia gestisce solo BookingException");
        }

        String userMessage = translateErrorCode(bookingException.getErrorCode());
        return ResponseEntity.badRequest().body(userMessage);
    }


    @Override
    public boolean canHandle(Exception exception) {
        return exception instanceof BookingException;
    }


    @Override
    public String getUseCaseType() {
        return USE_CASE_TYPE;
    }


    /**
     * Traduce il codice di errore in un messaggio comprensibile all'utente.
     */
    private String translateErrorCode(BookingErrorCode errorCode) {
        return switch (errorCode) {
            case BOOKING_NOT_FOUND -> "Prenotazione non trovata";
            case BOOKING_NOT_CANCELLABLE -> "La prenotazione non può essere annullata";
            case USER_NOT_AUTHORIZED -> "Non puoi annullare questa prenotazione perché non sei il proprietario.";
            case INVALID_INPUT_DATA -> "Dati di input non validi";
            case NOT_ENOUGH_SEATS -> "I posti disponibili sono insufficienti per la tua richiesta";
        };
    }
}
