package it.unibs.ingsw.destinazioni.application.rest.strategy;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import it.unibs.ingsw.destinazioni.application.exceptions.usecases.VisitPlanException;

@Component
public class VisitPlanExceptionHandlingStrategy implements ExceptionHandlingStrategy {

    private static final String USE_CASE_TYPE = "visitplan";

    @Override
    public ResponseEntity<String> handleException(Exception exception) {
        if (!(exception instanceof VisitPlanException visitPlanException)) {
            throw new IllegalArgumentException("Questa strategia gestisce solo VisitPlanException");
        }

        String userMessage = translateErrorCode(visitPlanException.getErrorCode());
        HttpStatus status = determineHttpStatus(visitPlanException.getErrorCode());

        return ResponseEntity.status(status).body(userMessage);
    }

    @Override
    public boolean canHandle(Exception exception) {
        return exception instanceof VisitPlanException;
    }

    @Override
    public String getUseCaseType() {
        return USE_CASE_TYPE;
    }

    private String translateErrorCode(
            it.unibs.ingsw.destinazioni.application.exceptions.codes.VisitPlanErrorCode errorCode) {
        return switch (errorCode) {
            case CANT_BE_CREATED -> "Il piano di visite non può essere creato";
            default -> "Errore interno, riprova più tardi";
        };
    }

    private HttpStatus determineHttpStatus(
            it.unibs.ingsw.destinazioni.application.exceptions.codes.VisitPlanErrorCode errorCode) {
        return switch (errorCode) {
            case CANT_BE_CREATED -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

}
