package it.unibs.ingsw.destinazioni.application.exceptions.usecases;

import it.unibs.ingsw.destinazioni.application.exceptions.base.BaseUseCaseException;
import it.unibs.ingsw.destinazioni.application.exceptions.codes.BookingErrorCode;

public class BookingException extends BaseUseCaseException {
    private static final String USE_CASE_TYPE = "booking";
    final BookingErrorCode errorCode;

    public BookingException(BookingErrorCode errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
    }

    public BookingException(BookingErrorCode errorCode, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
    }

    @Override
    public String getUseCaseType() {
        return USE_CASE_TYPE;
    }

    @Override
    public BookingErrorCode getErrorCode() {
        return errorCode;
    }
}
