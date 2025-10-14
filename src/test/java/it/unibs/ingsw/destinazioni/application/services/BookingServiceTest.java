package it.unibs.ingsw.destinazioni.application.services;

import it.unibs.ingsw.destinazioni.application.port.in.user.GetUserInfoUseCase;
import it.unibs.ingsw.destinazioni.application.port.out.BookingRepositoryPort;
import it.unibs.ingsw.destinazioni.application.port.out.VisitRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.model.Visit;
import it.unibs.ingsw.destinazioni.domain.model.VisitType;
import it.unibs.ingsw.destinazioni.domain.model.enums.VisitStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookingServiceTest {
    private BookingRepositoryPort bookingRepositoryMock;
    private VisitRepositoryPort visitRepositoryMock;
    private GetUserInfoUseCase getUserInfoUseCase;
    private Visit testVisit;

    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        bookingRepositoryMock = mock(BookingRepositoryPort.class);
        visitRepositoryMock = mock(VisitRepositoryPort.class);
        getUserInfoUseCase = mock(GetUserInfoUseCase.class);

        bookingService = new BookingService(bookingRepositoryMock, getUserInfoUseCase, visitRepositoryMock);

        VisitType visitType = new VisitType("", "", "", LocalDate.parse("2025-01-01"),
                null, LocalTime.parse("20:00"), 20, 5, 1, true, null, null);
        testVisit = new Visit(LocalDate.now(), null, visitType, null, VisitStatus.PROPOSED);
        when(visitRepositoryMock.findAll()).thenReturn(Set.of(testVisit));
    }

    @Test
    void bookVisit_ShouldThrowExceptionWhenWrongUserTriesBooking() {

    }

}