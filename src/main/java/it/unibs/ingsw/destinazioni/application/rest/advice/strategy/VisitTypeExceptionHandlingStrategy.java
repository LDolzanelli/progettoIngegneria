package it.unibs.ingsw.destinazioni.application.rest.advice.strategy;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import it.unibs.ingsw.destinazioni.application.exceptions.usecases.VisitTypeException;

@Component
public class VisitTypeExceptionHandlingStrategy implements ExceptionHandlingStrategy {

    private static final String USE_CASE_TYPE = "visittype";

    @Override
    public ResponseEntity<String> handleException(Exception exception) {
        if (!(exception instanceof VisitTypeException visitTypeException)) {
            throw new IllegalArgumentException("Questa strategia gestisce solo VisitTypeException");
        }

        String userMessage = translateErrorCode(visitTypeException.getErrorCode());
        HttpStatus status = determineHttpStatus(visitTypeException.getErrorCode());

        return ResponseEntity.status(status).body(userMessage);

    }

    private String translateErrorCode(
            it.unibs.ingsw.destinazioni.application.exceptions.codes.VisitTypeErrorCode errorCode) {
        return switch (errorCode) {
            case NOT_FOUND -> "Tipo di visita non trovato";
            case ALREADY_EXISTS -> "Esiste già un tipo di visita con questo titolo";
            case DOES_NOT_EXIST -> "Il tipo di visita non esiste";
            case CANT_BE_DELETED -> "Il tipo di visita non può essere eliminato";
            case CANT_BE_MODIFIED -> "Il tipo di visita non può essere modificato";
            case INVALID_LOCATION_FOR_VISIT_TYPE -> "La località specificata non è valida per questo tipo di visita";
            case VOLUNTEER_ALREADY_ASSIGNED -> "Volontario già assegnato a questo tipo di visita";
        };
    }

    private HttpStatus determineHttpStatus(
            it.unibs.ingsw.destinazioni.application.exceptions.codes.VisitTypeErrorCode errorCode) {
        return switch (errorCode) {
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case ALREADY_EXISTS -> HttpStatus.CONFLICT;
            case DOES_NOT_EXIST -> HttpStatus.BAD_REQUEST;
            case CANT_BE_DELETED -> HttpStatus.BAD_REQUEST;
            case CANT_BE_MODIFIED -> HttpStatus.BAD_REQUEST;
            case INVALID_LOCATION_FOR_VISIT_TYPE -> HttpStatus.BAD_REQUEST;
            case VOLUNTEER_ALREADY_ASSIGNED -> HttpStatus.BAD_REQUEST;
        };
    }

    @Override
    public boolean canHandle(Exception exception) {
        return exception instanceof VisitTypeException;
    }

    @Override
    public String getUseCaseType() {
        return USE_CASE_TYPE;
    }

}
