package it.unibs.ingsw.destinazioni.application.exceptions.specific;

import it.unibs.ingsw.destinazioni.application.exceptions.codes.VisitPlanErrorCode;
import it.unibs.ingsw.destinazioni.application.exceptions.generic.BaseUseCaseException;

public class VisitPlanException extends BaseUseCaseException {

    private final VisitPlanErrorCode errorCode;
    private static final String USE_CASE_TYPE = "visitplan";

    public VisitPlanException(VisitPlanErrorCode errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
    }


    public VisitPlanException(VisitPlanErrorCode errorCode, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
    }


    @Override
    public String getUseCaseType() {
        return USE_CASE_TYPE;
    }


    @Override
    public VisitPlanErrorCode getErrorCode() {
        return errorCode;
    }


}
