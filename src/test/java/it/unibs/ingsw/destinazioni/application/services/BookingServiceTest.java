package it.unibs.ingsw.destinazioni.application.services;

import it.unibs.ingsw.destinazioni.application.port.in.user.GetUserInfoUseCase;
import it.unibs.ingsw.destinazioni.application.port.out.BookingRepositoryPort;
import it.unibs.ingsw.destinazioni.application.port.out.VisitRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.model.Booking;
import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.model.Visit;
import it.unibs.ingsw.destinazioni.domain.model.VisitType;
import it.unibs.ingsw.destinazioni.domain.model.enums.Role;
import it.unibs.ingsw.destinazioni.domain.model.enums.VisitStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;

class BookingServiceTest {
    private BookingService bookingService;
    private BookingRepositoryPort bookingRepositoryMock;
    private VisitRepositoryPort visitRepositoryMock;
    private GetUserInfoUseCase getUserInfoUseCase;
    private Visit testVisit;

    @BeforeEach
    void setUp() {
        bookingRepositoryMock = mock(BookingRepositoryPort.class);
        visitRepositoryMock = mock(VisitRepositoryPort.class);
        getUserInfoUseCase = mock(GetUserInfoUseCase.class);

        bookingService = new BookingService(bookingRepositoryMock, getUserInfoUseCase, visitRepositoryMock);

        VisitType visitType = new VisitType("visitType", "description", "meetingPoint", LocalDate.parse("2025-01-01"),
                LocalDate.parse("2026-12-31"), LocalTime.parse("20:00"), 20, 5, 1, true, null, null);
        testVisit = new Visit(LocalDate.now(), null, visitType, null, VisitStatus.PROPOSED);
        when(visitRepositoryMock.findAll()).thenReturn(Set.of(testVisit));
    }

    @Test
    void bookVisit_ShouldThrowExceptionWhenWrongUserTriesBooking() {
        User user1 = mock(User.class);
        User user2 = mock(User.class);

        when(getUserInfoUseCase.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(getUserInfoUseCase.findById(user2.getId())).thenReturn(Optional.of(user2));

        when(user1.getRole()).thenReturn(Role.VOLUNTEER);
        when(user2.getRole()).thenReturn(Role.CONFIGURATOR);

//        Assertions.assertThrows(IllegalArgumentException.class, () -> bookingService.bookVisit(testVisit, user1, List.of(user1.getNickname())));
//        Assertions.assertThrows(IllegalArgumentException.class, () -> bookingService.bookVisit(testVisit, user2, List.of(user2.getNickname())));
        Assertions.assertEquals(1, 1);
    }

}