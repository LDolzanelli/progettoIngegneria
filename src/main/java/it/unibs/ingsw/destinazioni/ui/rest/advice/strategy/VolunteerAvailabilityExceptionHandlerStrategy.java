package it.unibs.ingsw.destinazioni.ui.rest.advice.strategy;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import it.unibs.ingsw.destinazioni.application.exceptions.specific.VolunteerAvailabilityException;

@Component
public class VolunteerAvailabilityExceptionHandlerStrategy implements ExceptionHandlingStrategy {

    private static final String USE_CASE_TYPE = "volunteeravailability";

    @Override
    public ResponseEntity<String> handleException(Exception exception) {
        if (!(exception instanceof VolunteerAvailabilityException availabilityException)) {
            throw new IllegalArgumentException("Questa strategia gestisce solo VolunteerAvailabilityException");
        }

        String userMessage = translateErrorCode(availabilityException.getErrorCode());
        HttpStatus status = determineHttpStatus(availabilityException.getErrorCode());

        return ResponseEntity.status(status).body(userMessage);
    }


    @Override
    public boolean canHandle(Exception exception) {
        return exception instanceof VolunteerAvailabilityException;
    }


    @Override
    public String getUseCaseType() {
        return USE_CASE_TYPE;
    }


    private String translateErrorCode(
            it.unibs.ingsw.destinazioni.application.exceptions.codes.VolunteerAvailabilityErrorCode errorCode) {
        return switch (errorCode) {
            case CANT_BE_ENABLED -> "La disponibilità dei volontari non può essere abilitata";
            case CANT_BE_DISABLED -> "La disponibilità dei volontari non può essere disabilitata";
            case NOT_ENABLED -> "La disponibilità dei volontari non è abilitata per il mese richiesto";
        };
    }


    private HttpStatus determineHttpStatus(
            it.unibs.ingsw.destinazioni.application.exceptions.codes.VolunteerAvailabilityErrorCode errorCode) {
        return switch (errorCode) {
            case CANT_BE_ENABLED -> HttpStatus.BAD_REQUEST;
            case CANT_BE_DISABLED -> HttpStatus.BAD_REQUEST;
            case NOT_ENABLED -> HttpStatus.BAD_REQUEST;
        };
    }



}


