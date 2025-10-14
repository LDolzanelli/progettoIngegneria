package it.unibs.ingsw.destinazioni.application.exceptions.specific;

import it.unibs.ingsw.destinazioni.application.exceptions.generic.BaseUseCaseException;
import it.unibs.ingsw.destinazioni.application.exceptions.codes.VolunteerAvailabilityErrorCode;

public class VolunteerAvailabilityException extends BaseUseCaseException {

    private static final String USE_CASE_TYPE = "volunteeravailability";
    private final it.unibs.ingsw.destinazioni.application.exceptions.codes.VolunteerAvailabilityErrorCode errorCode;

    public VolunteerAvailabilityException(VolunteerAvailabilityErrorCode errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
    }


    public VolunteerAvailabilityException(VolunteerAvailabilityErrorCode errorCode, String errorMessage,
            Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
    }


    @Override
    public String getUseCaseType() {
        return USE_CASE_TYPE;
    }


    @Override
    public VolunteerAvailabilityErrorCode getErrorCode() {
        return errorCode;
    }

}
