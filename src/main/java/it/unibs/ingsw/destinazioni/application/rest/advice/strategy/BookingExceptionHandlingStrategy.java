package it.unibs.ingsw.destinazioni.application.rest.advice.strategy;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import it.unibs.ingsw.destinazioni.application.exceptions.codes.BookingErrorCode;
import it.unibs.ingsw.destinazioni.application.exceptions.usecases.BookingException;


@Component
public class BookingExceptionHandlingStrategy implements ExceptionHandlingStrategy {

    private static final String USE_CASE_TYPE = "booking";

    @Override
    public ResponseEntity<String> handleException(Exception exception) {
        if (!(exception instanceof BookingException bookingException)) {
            throw new IllegalArgumentException("Questa strategia gestisce solo BookingException");
        }

        String userMessage = translateErrorCode(bookingException.getErrorCode());
        HttpStatus status = determineHttpStatus(bookingException.getErrorCode());
        return ResponseEntity.status(status).body(userMessage);
    }

    @Override
    public boolean canHandle(Exception exception) {
        return exception instanceof BookingException;
    }

    @Override
    public String getUseCaseType() {
        return USE_CASE_TYPE;
    }

    private String translateErrorCode(BookingErrorCode errorCode) {
        return switch (errorCode) {
            case BOOKING_NOT_FOUND -> "Prenotazione non trovata";
            case BOOKING_NOT_CANCELLABLE -> "La prenotazione non può essere annullata";
            case USER_NOT_AUTHORIZED -> "Non puoi annullare questa prenotazione perché non sei il proprietario.";
            case INVALID_INPUT_DATA -> "Dati di input non validi";
            case NOT_ENOUGH_SEATS -> "I posti disponibili sono insufficienti per la tua richiesta";
        };
    }

    private HttpStatus determineHttpStatus(BookingErrorCode errorCode) {
        return switch (errorCode) {
            case BOOKING_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case BOOKING_NOT_CANCELLABLE -> HttpStatus.CONFLICT;
            case USER_NOT_AUTHORIZED -> HttpStatus.FORBIDDEN;
            case INVALID_INPUT_DATA -> HttpStatus.BAD_REQUEST;
            case NOT_ENOUGH_SEATS -> HttpStatus.CONFLICT;
        };
    }
}
