// File: /ui/rest/advice/strategy/LocationExceptionHandlingStrategy.java
package it.unibs.ingsw.destinazioni.ui.rest.advice.strategy;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import it.unibs.ingsw.destinazioni.application.exceptions.specific.LocationException;
import it.unibs.ingsw.destinazioni.application.exceptions.codes.LocationErrorCode;

@Component
public class LocationExceptionHandlingStrategy implements ExceptionHandlingStrategy {

    private static final String USE_CASE_TYPE = "location";

    @Override
    public ResponseEntity<String> handleException(Exception exception) {
        if (!(exception instanceof LocationException locationException)) {
            throw new IllegalArgumentException("Questa strategia gestisce solo LocationException");
        }

        String userMessage = translateErrorCode(locationException.getErrorCode());
        HttpStatus status = determineHttpStatus(locationException.getErrorCode());

        return ResponseEntity.status(status).body(userMessage);
    }


    @Override
    public boolean canHandle(Exception exception) {
        return exception instanceof LocationException;
    }


    @Override
    public String getUseCaseType() {
        return USE_CASE_TYPE;
    }


    private String translateErrorCode(LocationErrorCode errorCode) {
        return switch (errorCode) {
            case LOCATION_NOT_FOUND -> "Località non trovata";
            case LOCATION_ALREADY_EXISTS -> "Esiste già una località con questo nome";
            case CANT_BE_DELETED -> "La località non può essere eliminata";
            case NULL_LOCATION -> "Errore interno, riprova più tardi";
        };
    }


    private HttpStatus determineHttpStatus(LocationErrorCode errorCode) {
        return switch (errorCode) {
            case LOCATION_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case LOCATION_ALREADY_EXISTS -> HttpStatus.CONFLICT;
            case CANT_BE_DELETED -> HttpStatus.BAD_REQUEST;
            case NULL_LOCATION -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
