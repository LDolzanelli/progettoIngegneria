package it.unibs.ingsw.destinazioni.application.exceptions.usecases;

import it.unibs.ingsw.destinazioni.application.exceptions.base.BaseUseCaseException;
import it.unibs.ingsw.destinazioni.application.exceptions.codes.VisitTypeErrorCode;

public class VisitTypeException extends BaseUseCaseException {
    private static final String USE_CASE_TYPE = "visittype";
    private final VisitTypeErrorCode errorCode;

    public VisitTypeException(VisitTypeErrorCode errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
    }

    public VisitTypeException(VisitTypeErrorCode errorCode, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
    }

    @Override
    public String getUseCaseType() {
        return USE_CASE_TYPE;
    }

    @Override
    public VisitTypeErrorCode getErrorCode() {
        return errorCode;
    }

}
