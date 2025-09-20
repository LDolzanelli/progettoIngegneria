package it.unibs.ingsw.destinazioni.application.exceptions;

import it.unibs.ingsw.destinazioni.application.exceptions.codes.BookingErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.StandardException;

@StandardException
@Getter
@RequiredArgsConstructor
public class BookingException extends RuntimeException {
    BookingErrorCode errorCode;
}

