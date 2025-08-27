package it.unibs.ingsw.destinazioni.ui.rest.advice;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import it.unibs.ingsw.destinazioni.application.exceptions.BookingException;

@Hidden
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BookingException.class)
    public ResponseEntity<String> handleBookingException(BookingException ex) {

        String userMessage;
        switch (ex.getErrorCode()) {
            case BOOKING_NOT_FOUND -> userMessage = "Prenotazione non trovata";
            case BOOKING_NOT_CANCELLABLE -> userMessage = "La prenotazione non può essere annullata";
            case USER_NOT_AUTHORIZED -> userMessage = "Non puoi annullare questa prenotazione perché non sei il proprietario.";
            case INVALID_INPUT_DATA -> userMessage = "Dati di input non validi";
            case NOT_ENOUGH_SEATS -> userMessage = "I posti disponibili sono insufficienti per la tua richiesta";
            default -> userMessage = "Errore nella gestione della prenotazione";
        }

        return ResponseEntity.badRequest().body(userMessage);
    }

    
}

