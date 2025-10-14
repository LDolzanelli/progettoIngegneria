package it.unibs.ingsw.destinazioni.application.exceptions.specific;

import it.unibs.ingsw.destinazioni.application.exceptions.codes.VisitDayErrorCode;
import it.unibs.ingsw.destinazioni.application.exceptions.generic.BaseUseCaseException;

public class VisitDayException extends BaseUseCaseException {

    private static final String USE_CASE_TYPE = "visit";
    private final VisitDayErrorCode errorCode;

    public VisitDayException(VisitDayErrorCode errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
    }


    public VisitDayException(VisitDayErrorCode errorCode, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
    }


    @Override
    public String getUseCaseType() {
        return USE_CASE_TYPE;
    }


    @Override
    public VisitDayErrorCode getErrorCode() {
        return errorCode;
    }

}
