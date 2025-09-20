package it.unibs.ingsw.destinazioni.application.exceptions;

import it.unibs.ingsw.destinazioni.application.exceptions.codes.BookingErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BookingException extends RuntimeException {
    final BookingErrorCode errorCode;

    public BookingException(BookingErrorCode errorCode, String errorMessage)
    {
        super(errorMessage);
        this.errorCode = errorCode;
    }
}

