package it.unibs.ingsw.destinazioni.application.exceptions;

import it.unibs.ingsw.destinazioni.application.exceptions.codes.BookingErrorCode;

public class BookingException extends RuntimeException {
    private final BookingErrorCode errorCode;

    public BookingException(BookingErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BookingErrorCode getErrorCode() {
        return errorCode;
    }
}

