package it.unibs.ingsw.destinazioni.application.exceptions.usecases;

import it.unibs.ingsw.destinazioni.application.exceptions.base.BaseUseCaseException;
import it.unibs.ingsw.destinazioni.application.exceptions.codes.VisitPlanErrorCode;

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
