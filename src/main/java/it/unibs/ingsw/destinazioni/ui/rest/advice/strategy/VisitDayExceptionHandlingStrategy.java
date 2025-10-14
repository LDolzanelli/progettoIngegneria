package it.unibs.ingsw.destinazioni.ui.rest.advice.strategy;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import it.unibs.ingsw.destinazioni.application.exceptions.specific.VisitDayException;

@Component
public class VisitDayExceptionHandlingStrategy implements ExceptionHandlingStrategy {
    private static final String USE_CASE_TYPE = "visit";

    @Override
    public ResponseEntity<String> handleException(Exception exception) {
        if (!(exception instanceof VisitDayException visitDayException)) {
            throw new IllegalArgumentException("Questa strategia gestisce solo VisitDayException");
        }

        String userMessage = translateErrorCode(visitDayException.getErrorCode());
        HttpStatus status = determineHttpStatus(visitDayException.getErrorCode());

        return ResponseEntity.status(status).body(userMessage);
    }


    @Override
    public boolean canHandle(Exception exception) {
        return exception instanceof VisitDayException;
    }


    @Override
    public String getUseCaseType() {
        return USE_CASE_TYPE;
    }


    private String translateErrorCode(
            it.unibs.ingsw.destinazioni.application.exceptions.codes.VisitDayErrorCode errorCode) {
        return switch (errorCode) {
            case VISIT_NOT_FOUND -> "Visita non trovata";
            case CANT_BE_CREATED_AT_THIS_DATE -> "Non è possibile creare una visita in questa data";
        };
    }


    private HttpStatus determineHttpStatus(
            it.unibs.ingsw.destinazioni.application.exceptions.codes.VisitDayErrorCode errorCode) {
        return switch (errorCode) {
            case VISIT_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case CANT_BE_CREATED_AT_THIS_DATE -> HttpStatus.BAD_REQUEST;
        };
    }



}
