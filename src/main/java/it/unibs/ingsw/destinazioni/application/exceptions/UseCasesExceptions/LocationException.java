package it.unibs.ingsw.destinazioni.application.exceptions.specific;

import it.unibs.ingsw.destinazioni.application.exceptions.generic.BaseUseCaseException;
import it.unibs.ingsw.destinazioni.application.exceptions.codes.LocationErrorCode;

public class LocationException extends BaseUseCaseException {
    private static final String USE_CASE_TYPE = "location";
    private final LocationErrorCode errorCode;

    public LocationException(LocationErrorCode errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
    }


    public LocationException(LocationErrorCode errorCode, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
    }


    @Override
    public String getUseCaseType() {
        return USE_CASE_TYPE;
    }


    @Override
    public LocationErrorCode getErrorCode() {
        return errorCode;
    }
}
