package it.unibs.ingsw.destinazioni.application.exceptions.specific;

import it.unibs.ingsw.destinazioni.application.exceptions.codes.UserErrorCode;
import it.unibs.ingsw.destinazioni.application.exceptions.generic.BaseUseCaseException;

public class UserException extends BaseUseCaseException {
    private static final String USE_CASE_TYPE = "user";
    private final UserErrorCode errorCode;

    public UserException(UserErrorCode errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
    }


    public UserException(UserErrorCode errorCode, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
    }


    @Override
    public String getUseCaseType() {
        return USE_CASE_TYPE;
    }


    @Override
    public UserErrorCode getErrorCode() {
        return errorCode;
    }
}
